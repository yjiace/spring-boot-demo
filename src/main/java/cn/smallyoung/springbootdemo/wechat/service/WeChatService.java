package cn.smallyoung.springbootdemo.wechat.service;


import cn.smallyoung.springbootdemo.wechat.client.WechatClient;
import cn.smallyoung.springbootdemo.wechat.response.Code2Session;
import cn.smallyoung.springbootdemo.wechat.response.Code2UserPhoneNumber;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author smallyoung
 */
@Slf4j
@Service
public class WeChatService {

    /**
     * 微信token对应redis的key
     */
    private static final String WECHAT_TOKEN_KEY = "wechat_token_key:";

    /**
     * 最大尝试连接次数
     */
    private static final Integer MAX_ATTEMPTS_COUNT = 3;

    @Value("${wechat.appid:''}")
    private String appid;

    @Value("${wechat.secret:''}")
    private String secret;

    @Resource
    private WechatClient wechatClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 该接口用于将 code 换取用户手机号。
     * 说明，每个 code 只能使用一次，code的有效期为5min。
     *
     * @param code 手机号获取凭证
     * @return 手机号
     */
    public Code2UserPhoneNumber getUserPhoneNumber(String code) {
        log.info("小程序授权用户手机号code:{}", code);
        Code2UserPhoneNumber code2UserPhoneNumber = wechatClient
                .getUserPhoneNumber(this.getWechatToken(), JSONObject.of("code", code));
        log.info("小程序授权用户手机号返回结果：{}", code2UserPhoneNumber);
        return code2UserPhoneNumber;
    }

    /**
     * 该接口用于将 code 换取登录凭证
     * 说明，每个 code 只能使用一次，code的有效期为5min。
     *
     * @param code 登录时获取的 code，可通过wx.login获取
     * @return 登录凭证包括openid+unionid
     */
    public Code2Session jsCode2session(String code) {
        log.info("小程序授权登录code:{}", code);
        String str = wechatClient.jsCode2session(appid, secret, code, "authorization_code");
        Code2Session code2Session = JSONObject.parseObject(str, Code2Session.class);
        log.info("小程序授权登录返回结果：{}", code2Session);
        return code2Session;
    }

    /**
     * 获取微信Token
     *
     * @return 微信token
     */
    public String getWechatToken() {
        String key = getWechatTokenKey();
        Object obj = redisTemplate.boundValueOps(key).get();
        if (obj == null) {
            return refreshWechatToken();
        }
        return obj.toString();
    }

    /**
     * 刷新微信Token，只有当token失效后才可调用，否则容易超过接口调用次数限制
     *
     * @return 微信token
     */
    public String refreshWechatToken() {
        String token = null;
        JSONObject data;
        //重试3次
        for (int i = 0; i < MAX_ATTEMPTS_COUNT; i++) {
            data = wechatClient.wechatToken("client_credential", appid, secret);
            log.info("获取微信access_token：{}", data);
            token = data.getString("access_token");
            if (token != null && !token.isEmpty()) {
                redisTemplate.boundValueOps(getWechatTokenKey()).set(token, data.getLong("expires_in") - (5 * 60), TimeUnit.SECONDS);
                break;
            }
        }
        return token;
    }

    /**
     * 获取微信小程序Redis的Key
     *
     * @return 拼接完成后的redis的key值
     */
    private String getWechatTokenKey() {
        return WECHAT_TOKEN_KEY + appid + "_" + secret;
    }

}
