package cn.smallyoung.springbootdemo.user.controller;


import cn.hutool.core.lang.tree.Tree;
import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import cn.smallyoung.springbootdemo.user.entity.Permission;
import cn.smallyoung.springbootdemo.user.pojo.PermissionRequest;
import cn.smallyoung.springbootdemo.user.pojo.PermissionResponse;
import cn.smallyoung.springbootdemo.user.pojo.mapper.PermissionMapper;
import cn.smallyoung.springbootdemo.user.service.PermissionService;
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
@RequestMapping("permissions")
public class PermissionController {


    @Resource
    private PermissionService permissionService;


    /**
     * 生成权限树
     *
     * @return 权限树
     */
    @GetMapping("/tree")
    public List<Tree<String>> tree(HttpServletRequest request) {
        return permissionService.toTree(WebUtils.getParametersStartingWith(request, "search_"));
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Page<PermissionResponse> page(@PageableDefault(sort = {"updatedTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                         HttpServletRequest request) {
        Page<Permission> page = permissionService.findAll(WebUtils.getParametersStartingWith(request, "search_"), pageable);
        if (CollectionUtils.isEmpty(page.getContent())) {
            return new PageImpl<>(List.of(), pageable, page.getTotalElements());
        }
        return UserUtil.setUserName(page.map(PermissionMapper.INSTANCE::toResponse));
    }

    /**
     * 根据id查询详情
     *
     * @param id 权限id
     */
    @GetMapping("/findById/{id}")
    public PermissionResponse findById(@PathVariable String id) {
        Permission permission = permissionService.findOne(id);
        if (permission == null) {
            throw new BizException("根据ID【{}】为查询到对应的权限信息", id);
        }
        return UserUtil.setUserName(PermissionMapper.INSTANCE.toResponse(permission));
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public void save(@RequestBody PermissionRequest request) {
        Permission permission = new Permission();
        if (request.getId() != null) {
            permission = permissionService.findOne(request.getId());
            if (permission == null) {
                throw new BizException("根据ID【{}】为查询到对应的权限信息", request.getId());
            }
        }
        PermissionMapper.INSTANCE.init(permission, request);
        permissionService.save(permission);
    }

    /**
     * 删除
     *
     * @param ids 权限id列表
     */
    @DeleteMapping("/delete")
    public void delete(@RequestBody List<String> ids) {
        List<Permission> permissions = permissionService.findAllById(ids);
        permissions.forEach(t -> t.setDeleted("Y"));
        permissionService.save(permissions);
    }
}
