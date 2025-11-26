package cn.smallyoung.springbootdemo;


import cn.smallyoung.springbootdemo.user.entity.Role;
import cn.smallyoung.springbootdemo.user.service.RoleService;
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
public class RoleTest {

    @Resource
    private RoleService roleService;


    /**
     * 分页查询
     */
    @Test
    public void page() {
        Page<Role> page = roleService.findAll(Map.of(), Pageable.unpaged());
        System.out.println(page.getContent());
    }

    /**
     * 根据id查询详情
     *
     */
    @Test
    public void findById() {
        Role role = roleService.findOne("6926c5bb8f79996afe1104cc");
        System.out.println(role);
    }

    /**
     * 保存
     */
    @Test
    public void save() {
        Role role =  new Role();
        role.setName("管理员");
        role.setCreatedBy("-1");
        role.setCreatedTime(LocalDateTime.now());
        role.setUpdatedBy("-1");
        role.setUpdatedTime(LocalDateTime.now());
        role.setDeleted("N");
        roleService.save(role);
    }

    /**
     * 删除
     *
     */
    @Test
    public void delete() {
        List<Role> roles = roleService.findAllById(List.of("6925755dfef93b7155e91ff7"));
        roles.forEach(t -> t.setDeleted("Y"));
        roleService.save(roles);
    }

}
