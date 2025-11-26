package cn.smallyoung.springbootdemo.user.service;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.smallyoung.springbootdemo.base.BaseService;
import cn.smallyoung.springbootdemo.config.JwtConfig;
import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.user.dao.RoleRepository;
import cn.smallyoung.springbootdemo.user.dao.UserRepository;
import cn.smallyoung.springbootdemo.user.entity.Permission;
import cn.smallyoung.springbootdemo.user.entity.Role;
import cn.smallyoung.springbootdemo.user.entity.User;
import cn.smallyoung.springbootdemo.util.UserUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author smallyoung
 */

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService extends BaseService<User, String> {


    @Resource
    private JwtConfig jwtConfig;
    @Resource
    private RoleRepository roleRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 后台系统登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    public Dict loginByUsername(String username, String password, boolean remember) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("根据用户名【{}】暂未查询到用户信息", username);
            throw new BizException("根据用户名未查询到用户信息");
        }
        if (StrUtil.isBlank(user.getPassword())) {
            log.error("用户【{}】暂未设置密码，请点击“忘记”进行重置", username);
            throw new BizException("用户暂未设置密码");
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            log.error("用户【{}】登录系统，密码【{}】错误", user.getId(), password);
            throw new BizException("密码错误");
        }
        return this.login(user, remember);
    }

    /**
     * 登录核心方法
     *
     * @param user     用户对象
     * @param remember 是否记住，记住密码则不设置过期时间
     */
    public Dict login(User user, boolean remember) {
        if ("N".equals(user.getStatus())) {
            throw new BizException("本账号已被冻结，请联系管理员！");
        }
        List<Permission> permissions = user.getAllPermission();
        // 生成菜单树
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setWeightKey("orderNum");
        List<Tree<String>> permissionTree = TreeUtil.build(permissions.stream()
                        .filter(p -> !"button".equals(p.getType())).toList(), "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setName(treeNode.getName());
                    tree.putExtra("val", treeNode.getVal());
                    tree.putExtra("type", treeNode.getType());
                    tree.putExtra("jumpPath", treeNode.getJumpPath());
                    tree.putExtra("icon", treeNode.getIcon());
                    tree.setWeight(treeNode.getOrderNum());
                });

        //生成Token
        RSA privateKey = new RSA(jwtConfig.getPrivateKey(), null);
        JWT jwt = JWT.create()
                //签发时间
                .setIssuedAt(DateUtil.date())
                //签发时间
                .setIssuedAt(DateUtil.date())
                //签名
                .setSigner(JWTSignerUtil.rs256(privateKey.getPrivateKey()))
                .setPayload(jwtConfig.getUserName(), user.getId());
        if (!remember) {
            jwt.setExpiresAt(DateUtil.offsetMinute(DateTime.now(), jwtConfig.getExpiration()));
        }
        String token = jwt.sign();
        //配置权限
        Dict authority = Dict.create().set(jwtConfig.getTokenHead(), token).set(jwtConfig.getAuthorityName(),
                permissions.stream().filter(p -> p.getVal() != null)
                        .flatMap(p -> Stream.of(p.getVal().split(",")))
                        .distinct().collect(Collectors.joining(",")));
        if (token != null && jwtConfig.getEnable()) {
            String redisKey = UserUtil.getLoginRedisKey(user.getId(), token);
            if (remember) {
                redisTemplate.opsForValue().set(redisKey, authority);
            } else {
                redisTemplate.opsForValue().set(redisKey, authority, jwtConfig.getExpiration(), TimeUnit.MINUTES);
            }
        }
        return Dict.create().set("id", user.getId()).set("token", token).set("username", user.getUsername()).set("avatarUrl", user.getAvatarUrl())
                .set("remember", remember).set("permissions", permissionTree).set("identification", permissions.stream()
                        .filter(p -> "button".equals(p.getType())).map(Permission::getIdentification).filter(Objects::nonNull).distinct().toList());
    }

    /**
     * 刷新token
     *
     * @return 刷新后的用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Dict refresh() {
        String userId = UserUtil.getCurrentAuditor();
        if ("-1".equals(userId)) {
            throw new BizException("获取用户信息失败！");
        }
        User user = this.findOne(userId);
        return this.login(user, false);
    }

    /**
     * 登出
     */
    @Transactional(rollbackFor = Exception.class)
    public void logout() {
        String userId = UserUtil.getCurrentAuditor();
        if ("-1".equals(userId)) {
            throw new BizException("获取用户信息失败！");
        }
        if (jwtConfig.getEnable() && jwtConfig.getSso()) {
            redisTemplate.delete(UserUtil.getLoginRedisKey(userId, UserUtil.getCurrentUserToken()));
        }
    }

    /**
     * 查询用户角色列表和所有角色列表
     *
     * @param userId 用户id
     * @return 用户角色列表和所有角色列表
     */
    public Dict getRoles(String userId) {
        User user = super.findOne(userId);
        List<Role> userRoles = user.getRoles();
        List<String> userRoleIds = new ArrayList<>();
        if (userRoles != null) {
            userRoleIds = userRoles.stream().map(Role::getId).toList();
        }
        return Dict.create().set("roles", roleRepository.findAll())
                .set("userRoleIds", userRoleIds);
    }

    /**
     * 为用户赋予角色
     *
     * @param userId  用户id
     * @param roleIds 角色id
     */
    @Transactional(rollbackFor = Exception.class)
    public void giveRole(String userId, List<String> roleIds) {
        User user = super.findOne(userId);
        if (user == null) {
            throw new BizException("根据id【】没有查询到该用户", userId);
        }
        if (CollectionUtils.isEmpty(roleIds) && CollectionUtils.isEmpty(user.getRoles())) {
            return;
        }
        if (CollectionUtils.isEmpty(roleIds)) {
            user.setRoles(null);
        } else {
            user.setRoles(roleRepository.findAllById(roleIds));
        }
        super.save(user);
    }

}
