package cn.smallyoung.springbootdemo.user.entity;


import cn.smallyoung.springbootdemo.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author smallyoung
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction(" deleted = 'N' ")
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_user", schema = "public")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class User extends BaseEntity<String> implements Serializable {


    @Serial
    private static final long serialVersionUID = 4697369047001746474L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    @Column(name = "password")
    private String password;

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
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * 状态，Y正常，N冻结
     */
    @Column(name = "status")
    private String status;

}
