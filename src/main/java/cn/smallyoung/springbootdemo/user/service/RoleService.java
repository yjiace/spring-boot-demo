package cn.smallyoung.springbootdemo.user.service;


import cn.smallyoung.springbootdemo.base.BaseService;
import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.user.dao.RoleRepository;
import cn.smallyoung.springbootdemo.user.entity.Role;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *
 * @author smallyoung
 */

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleService extends BaseService<Role, String> {

    @Resource
    private RoleRepository roleRepository;
    @Resource
    private PermissionService permissionService;

    /**
     * 设置权限
     *
     * @param id            角色id
     * @param permissionIds 权限id列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void givePermission(String id, List<String> permissionIds) {
        Role role = super.findOne(id);
        if (role == null) {
            throw new BizException("根据ID【{}】未查询到角色信息！", id);
        }
        if (CollectionUtils.isEmpty(permissionIds)) {
            role.setPermissions(null);
        } else {
            role.setPermissions(permissionService.findAllById(permissionIds));
        }
        super.save(role);
    }
}
