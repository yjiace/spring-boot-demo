package cn.smallyoung.springbootdemo.user.dao;


import cn.smallyoung.springbootdemo.base.BaseRepository;
import cn.smallyoung.springbootdemo.user.entity.User;

/**
 *
 * @author smallyoung
 */
public interface UserRepository extends BaseRepository<User, String> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

}
