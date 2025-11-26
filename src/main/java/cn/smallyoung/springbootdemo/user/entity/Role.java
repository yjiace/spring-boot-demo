package cn.smallyoung.springbootdemo.user.entity;

import cn.smallyoung.springbootdemo.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色表
 *
 * @author : smallyoung
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction(" deleted = 'N' ")
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_role", schema = "public")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Role extends BaseEntity<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7606677195322565491L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 角色名称
     */
    @Column(name = "name")
    private String name;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "t_role_permission", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private List<Permission> permissions = new ArrayList<>();

}