package cn.smallyoung.springbootdemo;


import cn.smallyoung.springbootdemo.user.entity.User;
import cn.smallyoung.springbootdemo.user.service.UserService;
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
public class UserTest {

    @Resource
    private UserService userService;

    /**
     * 分页查询
     */
    @Test
    public void page() {
        Page<User> page = userService.findAll(Map.of(), Pageable.unpaged());
        System.out.println(page.getContent());
    }

    /**
     * 根据id查询详情
     *
     */
    @Test
    public void findById() {
        User user = userService.findOne("6925755dfef93b7155e91ff7");
        System.out.println(user);
    }

    /**
     * 保存
     */
    @Test
    public void save() {
        User user =  new User();
        user.setUsername("test");
        user.setPassword("password");
        user.setStatus("Y");
        user.setCreatedBy("-1");
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedBy("-1");
        user.setUpdatedTime(LocalDateTime.now());
        user.setDeleted("N");
        userService.save(user);
    }

    /**
     * 删除
     *
     */
    @Test
    public void delete() {
        List<User> users = userService.findAllById(List.of("6925755dfef93b7155e91ff7"));
        users.forEach(t -> t.setDeleted("Y"));
        userService.save(users);
    }
}
