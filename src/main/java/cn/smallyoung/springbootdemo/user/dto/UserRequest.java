package cn.smallyoung.springbootdemo.user.dto;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author smallyoung
 */

@Data
public class UserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3582391220534037823L;

    /**
     * 主键
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 状态，Y正常，N冻结
     */
    private String status;

}
