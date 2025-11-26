package cn.smallyoung.springbootdemo;


import cn.smallyoung.springbootdemo.user.entity.Permission;
import cn.smallyoung.springbootdemo.user.service.PermissionService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *
 * @author smallyoung
 */

@SpringBootTest
@ActiveProfiles("dev")
public class PermissionTest {

    @Resource
    private PermissionService permissionService;


    /**
     * 分页查询
     */
    @Test
    public void page() {
        Page<Permission> page = permissionService.findAll(Map.of(), Pageable.unpaged());
        System.out.println(page.getContent());
    }

    /**
     * 根据id查询详情
     *
     */
    @Test
    public void findById() {
        Permission permission = permissionService.findOne("6926c81d8f79d5db9a7e3103");
        System.out.println(permission);
    }

    /**
     * 保存
     */
    @Test
    public void save() {
        Permission permission =  new Permission();
        permission.setName("全部权限");
        permission.setParentId("0");
        permission.setVal("/**");
        permission.setType("catalogue");
        permission.setOrderNum(0);
        permission.setCreatedBy("-1");
        permission.setCreatedTime(LocalDateTime.now());
        permission.setUpdatedBy("-1");
        permission.setUpdatedTime(LocalDateTime.now());
        permission.setDeleted("N");
        permissionService.save(permission);
    }

    /**
     * 删除
     *
     */
    @Test
    public void delete() {
        List<Permission> permissions = permissionService.findAllById(List.of("6925755dfef93b7155e91ff7"));
        permissions.forEach(t -> t.setDeleted("Y"));
        permissionService.save(permissions);
    }

}
