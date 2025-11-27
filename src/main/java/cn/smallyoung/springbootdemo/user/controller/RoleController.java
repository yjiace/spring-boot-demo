package cn.smallyoung.springbootdemo.user.controller;


import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import cn.smallyoung.springbootdemo.user.entity.Role;
import cn.smallyoung.springbootdemo.user.dto.RoleRequest;
import cn.smallyoung.springbootdemo.user.dto.RoleResponse;
import cn.smallyoung.springbootdemo.user.dto.mapper.RoleMapper;
import cn.smallyoung.springbootdemo.user.service.RoleService;
import cn.smallyoung.springbootdemo.util.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("role")
public class RoleController {

    @Resource
    private RoleService roleService;


    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Page<RoleResponse> page(@PageableDefault(sort = {"updatedTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                   HttpServletRequest request) {
        Page<Role> page = roleService.findAll(WebUtils.getParametersStartingWith(request, "search_"), pageable);
        if (CollectionUtils.isEmpty(page.getContent())) {
            return new PageImpl<>(List.of(), pageable, page.getTotalElements());
        }
        return UserUtil.setUserName(page.map(RoleMapper.INSTANCE::toResponse));
    }

    /**
     * 根据id查询详情
     *
     * @param id 角色id
     */
    @GetMapping("/findById/{id}")
    public RoleResponse findById(@PathVariable String id) {
        Role role = roleService.findOne(id);
        if (role == null) {
            throw new BizException("根据ID【{}】为查询到对应的角色信息", id);
        }
        return UserUtil.setUserName(RoleMapper.INSTANCE.toResponse(role));

    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public void save(@RequestBody RoleRequest request) {
        Role role = new Role();
        if (request.getId() != null) {
            role = roleService.findOne(request.getId());
            if (role == null) {
                throw new BizException("根据ID【{}】为查询到对应的角色信息", request.getId());
            }
        }
        RoleMapper.INSTANCE.init(role, request);
        roleService.save(role);
    }

    /**
     * 删除
     *
     * @param ids 角色id列表
     */
    @DeleteMapping("/delete")
    public void delete(@RequestBody List<String> ids) {
        List<Role> roles = roleService.findAllById(ids);
        roles.forEach(t -> t.setDeleted("Y"));
        roleService.save(roles);
    }

    /**
     * 设置权限
     *
     * @param id            角色id
     * @param permissionIds 权限id列表
     */
    @PostMapping("/givePermission/{id}")
    public void givePermission(@PathVariable String id, @RequestBody List<String> permissionIds) {
        roleService.givePermission(id, permissionIds);
    }


}
