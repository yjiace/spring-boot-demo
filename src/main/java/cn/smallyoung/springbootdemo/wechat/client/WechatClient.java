package cn.smallyoung.springbootdemo.wechat.client;


import cn.smallyoung.springbootdemo.wechat.response.Code2UserPhoneNumber;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 *
 * @author smallyoung
 */
@HttpExchange
public interface WechatClient {

    /**
     * 该接口用于将 code 换取登录凭证
     * 说明，每个 code 只能使用一次，code的有效期为5min。
     *
     * @param appid 小程序 appId
     * @param secret 小程序 appSecret
     * @param jsCode 登录时获取的 code，可通过wx.login获取
     * @param grantType 授权类型
     * @return 登录凭证包括openid+unionid
     */
    @GetExchange("https://api.weixin.qq.com/sns/jscode2session")
    String jsCode2session(@RequestParam("appid") String appid, @RequestParam("secret") String secret,
                                @RequestParam("js_code") String jsCode,
                                @RequestParam(value = "grant_type", defaultValue = "authorization_code") String grantType);


    /**
     * 获取微信小程序Token
     *
     * @param grantType 固定值client_credential
     * @param appid     小程序唯一凭证，即 AppID
     * @param secret    小程序唯一凭证密钥
     * @return 微信小程序返回结果
     */
    @GetExchange("https://api.weixin.qq.com/cgi-bin/token")
    JSONObject wechatToken(@RequestParam("grant_type") String grantType,
                           @RequestParam("appid") String appid, @RequestParam("secret") String secret);


    /**
     * 该接口用于将 code 换取用户手机号。
     * 说明，每个 code 只能使用一次，code的有效期为5min。
     *
     * @param accessToken 接口调用凭证
     * @param data        手机号获取凭证
     * @return 手机号
     */
    @PostExchange("https://api.weixin.qq.com/wxa/business/getuserphonenumber")
    Code2UserPhoneNumber getUserPhoneNumber(@RequestParam("access_token") String accessToken, @RequestBody JSONObject data);


}
