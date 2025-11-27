package cn.smallyoung.springbootdemo.user.dto;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author smallyoung
 */

@Data
public class LoginByUsernameRequest implements Serializable {


    @Serial
    private static final long serialVersionUID = -5417587270842558267L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码id
     */
    private String codeId;

    /**
     * 是否记住密码
     */
    private Boolean remember = false;
}
