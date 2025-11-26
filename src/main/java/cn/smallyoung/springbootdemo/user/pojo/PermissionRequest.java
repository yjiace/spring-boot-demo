package cn.smallyoung.springbootdemo.user.pojo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author smallyoung
 */
@Getter
@Setter
@NoArgsConstructor
public class PermissionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8642391011471132921L;

    /**
     * 主键
     */
    private String id;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限值
     */
    private String val;

    /**
     * 权限标识,前端用于控制按钮显隐
     */
    private String identification;

    /**
     * 权限图标
     */
    private String icon;

    /**
     * 跳转路径
     */
    private String jumpPath;

    /**
     * 权限类型 目录:catalogue,菜单:menu,按钮:button
     */
    private String type;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 备注
     */
    private String remark;
}
