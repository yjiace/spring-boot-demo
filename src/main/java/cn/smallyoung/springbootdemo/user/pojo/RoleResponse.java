package cn.smallyoung.springbootdemo.user.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author smallyoung
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -5734947136061876490L;

    /**
     * 主键
     */
    private String id;

    /**
     * 角色名称
     */
    private String name;

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
