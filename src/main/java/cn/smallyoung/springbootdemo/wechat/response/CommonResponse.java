package cn.smallyoung.springbootdemo.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author smallyoung
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommonResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -9197906160464418803L;

    /**
     * 返回码
     */
    @JsonProperty("errcode")
    private Integer errCode;

    /**
     * 对返回码的文本描述内容
     */
    @JsonProperty("errmsg")
    private String errMsg;
}
