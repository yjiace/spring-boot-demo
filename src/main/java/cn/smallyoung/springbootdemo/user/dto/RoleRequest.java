package cn.smallyoung.springbootdemo.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author smallyoug
 */

@Getter
@Setter
@NoArgsConstructor
public class RoleRequest implements Serializable  {

    @Serial
    private static final long serialVersionUID = 1801394844327405803L;

    /**
     * 主键
     */
    private String id;

    /**
     * 角色名
     */
    private String name;
}
