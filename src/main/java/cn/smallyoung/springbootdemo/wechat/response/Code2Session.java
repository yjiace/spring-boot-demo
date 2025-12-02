package cn.smallyoung.springbootdemo.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信小程序，code转登录凭证
 *
 * @author smallyoung
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Code2Session extends CommonResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 6200720983387964519L;

    /**
     * 会话密钥
     */
    @JsonProperty("session_key")
    private String sessionKey;

    /**
     * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回\
     */
    private String unionid;

    /**
     * 用户唯一标识
     */
    private String openid;


}
