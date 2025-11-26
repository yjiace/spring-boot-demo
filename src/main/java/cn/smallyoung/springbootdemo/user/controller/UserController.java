package cn.smallyoung.springbootdemo.user.controller;


import cn.hutool.core.lang.Dict;
import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import cn.smallyoung.springbootdemo.user.entity.User;
import cn.smallyoung.springbootdemo.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Page<User> page(@PageableDefault(sort = {"updatedTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                           HttpServletRequest request) {
        return userService.findAll(WebUtils.getParametersStartingWith(request, "search_"), pageable);
    }

    /**
     * 根据id查询详情
     *
     * @param id 角色id
     */
    @GetMapping("/findById/{id}")
    public User findById(@PathVariable String id) {
        return userService.findOne(id);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public void save(@RequestBody User user) {
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
