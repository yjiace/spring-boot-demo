package cn.smallyoung.springbootdemo.user.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
     * 手机号
     */
    private String mobile;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 状态，Y正常，N冻结
     */
    private String status;

    /**
     * 创建人(ID)
     */
    private String createdBy;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime createdTime;

    /**
     * 更新人(ID)
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime updatedTime;

}
