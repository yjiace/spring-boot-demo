package cn.smallyoung.springbootdemo.user.controller;


import cn.smallyoung.springbootdemo.exception.BizException;
import cn.hutool.core.lang.Dict;
import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import cn.smallyoung.springbootdemo.user.dto.UserRequest;
import cn.smallyoung.springbootdemo.user.dto.UserResponse;
import cn.smallyoung.springbootdemo.user.dto.mapper.UserMapper;
import cn.smallyoung.springbootdemo.user.entity.User;
import cn.smallyoung.springbootdemo.user.dto.UserRequest;
import cn.smallyoung.springbootdemo.user.dto.UserResponse;
import cn.smallyoung.springbootdemo.user.dto.mapper.UserMapper;
import cn.smallyoung.springbootdemo.user.service.UserService;
import cn.smallyoung.springbootdemo.util.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.List;

/**
 *
 * @author smallyoung
 */

@Slf4j
@RestController
@ResponseSysResult
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Page<UserResponse> page(@PageableDefault(sort = {"updatedTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                   HttpServletRequest request) {
        Page<User> page = userService.findAll(WebUtils.getParametersStartingWith(request, "search_"), pageable);
        if (CollectionUtils.isEmpty(page.getContent())) {
            return new PageImpl<>(List.of(), pageable, page.getTotalElements());
        }
        return UserUtil.setUserName(page.map(UserMapper.INSTANCE::toResponse));
    }

    /**
     * 根据id查询详情
     *
     * @param id 角色id
     */
    @GetMapping("/findById/{id}")
    public UserResponse findById(@PathVariable String id) {
        User user = userService.findOne(id);
        if (user == null) {
            throw new BizException("根据ID【{}】为查询到对应的用户信息", id);
        }
        return UserUtil.setUserName(UserMapper.INSTANCE.toResponse(user));
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public void save(@RequestBody UserRequest request) {
        User user = new User();
        if (request.getId() != null) {
            user = userService.findOne(request.getId());
            if (user == null) {
                throw new BizException("根据ID【{}】为查询到对应的用户信息", request.getId());
            }
            request.setPassword(null);
        }else{
            request.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        }
        UserMapper.INSTANCE.init(user, request);
        userService.save(user);
    }

    /**
     * 删除
     *
     * @param ids 角色id列表
     */
    @DeleteMapping("/delete")
    public void delete(@RequestBody List<String> ids) {
        List<User> users = userService.findAllById(ids);
        users.forEach(t -> t.setDeleted("Y"));
        userService.save(users);
    }

    /**
     * 刷新token
     *
     * @return 刷新后的用户信息
     */
    @GetMapping("refresh")
    public Dict refresh() {
        return userService.refresh();
    }

    /**
     * 登出
     */
    @DeleteMapping("logout")
    public void logout() {
        userService.logout();
    }

    /**
     * 查询用户角色列表和所有角色列表
     *
     * @param userId 用户id
     * @return 用户角色列表和所有角色列表
     */
    @GetMapping("getRoleByUserId/{userId}")
    public Dict getRoleByUserId(@PathVariable String userId) {
        return userService.getRoles(userId);
    }

    /**
     * 为用户赋予角色
     *
     * @param userId  用户id
     * @param roleIds 角色id列表
     */
    @PostMapping("giveRole/{userId}")
    public void giveRole(@PathVariable String userId, @RequestBody List<String> roleIds) {
        userService.giveRole(userId, roleIds);
    }

}
