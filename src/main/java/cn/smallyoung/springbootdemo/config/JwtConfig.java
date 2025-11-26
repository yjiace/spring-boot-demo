package cn.smallyoung.springbootdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author smallyoung
 */

@Data
@Configuration
@ConfigurationProperties("jwt")
public class JwtConfig {


    /**
     * 是否开启鉴权，当为false时，表示通过全部请求
     */
    private Boolean enable = true;

    /**
     * 是否单点登录
     */
    private Boolean sso = false;

    /**
     * token请求头名称
     */
    private String tokenHead = "Authorization";

    /**
     * redis中保存token的键名称
     */
    private String redisKey = "login_token_key:";

    /**
     * token过期时间（分钟）
     */
    private Integer expiration = 120;

    /**
     * 允许刷新Token的过期最大时长（秒）
     * 默认30天
     */
    private Integer refreshTime = 60 * 60 * 24 * 30;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 只要登录即可访问
     */
    private List<String> authUrl = Arrays.asList("/user/logout", "/user/updatePassword", "/user/refresh");

    /**
     * 匿名访问
     */
    private List<String> anonUrl = List.of("/captcha", "/login");

    /**
     * 禁止外网访问接口
     */
    private List<String> noAccess = new ArrayList<>();

    /**
     * 签名中的用户名key
     */
    private String userName = "username";

    /**
     * 签名中的权限key
     */
    private String authorityName = "authorities";

}
