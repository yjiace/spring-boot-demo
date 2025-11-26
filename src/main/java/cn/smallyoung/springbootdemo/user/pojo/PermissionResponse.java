package cn.smallyoung.springbootdemo.user.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author smallyoung
 */
@Getter
@Setter
@NoArgsConstructor
public class PermissionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1448656431980122256L;

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
