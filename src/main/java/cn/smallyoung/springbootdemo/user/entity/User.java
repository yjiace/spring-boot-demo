package cn.smallyoung.springbootdemo.user.entity;

import cn.smallyoung.springbootdemo.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
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
    private static final long serialVersionUID = 4632193998951213972L;

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

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "t_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roles = new ArrayList<>();

    @JsonIgnore
    public List<Permission> getAllPermission() {
        if (roles == null) {
            return new ArrayList<>();
        }
        return this.roles.stream().filter(r -> r.getPermissions() != null)
                .flatMap(r -> r.getPermissions().stream()).filter(Objects::nonNull).distinct()
                .sorted(Comparator.comparing(Permission::getOrderNum)).collect(Collectors.toList());
    }

}
