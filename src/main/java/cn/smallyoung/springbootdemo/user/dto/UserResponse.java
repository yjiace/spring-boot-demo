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
public class UserResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -803884227659931598L;

    /**
     * 主键
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 微信openid
     */
    private String openid;

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
     * 创建人姓名
     */
    private String createdName;


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
     * 更新人姓名
     */
    private String updatedName;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime updatedTime;
}
