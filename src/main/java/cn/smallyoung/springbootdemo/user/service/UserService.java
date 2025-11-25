package cn.smallyoung.springbootdemo.user.service;


import cn.smallyoung.springbootdemo.base.BaseService;
import cn.smallyoung.springbootdemo.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author smallyoung
 */

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService extends BaseService<User, String> {
}
