package cn.smallyoung.springbootdemo.user.entity;

import cn.smallyoung.springbootdemo.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限表
 *
 * @author : smallyoung
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction(" deleted = 'N' ")
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_permission", schema = "public")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Permission extends BaseEntity<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1120589645268591055L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 父级id
     */
    @Column(name = "parent_id")
    private String parentId;

    /**
     * 权限名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 权限值
     */
    @Column(name = "val")
    private String val;

    /**
     * 权限标识,前端用于控制按钮显隐
     */
    @Column(name = "identification")
    private String identification;

    /**
     * 权限图标
     */
    @Column(name = "icon")
    private String icon;

    /**
     * 跳转路径
     */
    @Column(name = "jump_path")
    private String jumpPath;

    /**
     * 权限类型 目录:catalogue,菜单:menu,按钮:button
     */
    @Column(name = "type")
    private String type;

    /**
     * 排序
     */
    @Column(name = "order_num")
    private Integer orderNum;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;
}