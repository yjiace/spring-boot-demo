package cn.smallyoung.springbootdemo.wechat.controller;


import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import cn.smallyoung.springbootdemo.wechat.response.Code2Session;
import cn.smallyoung.springbootdemo.wechat.response.Code2UserPhoneNumber;
import cn.smallyoung.springbootdemo.wechat.service.WeChatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author smallyoung
 */
@RestController
@ResponseSysResult
@RequestMapping("wechat")
public class WeChatController {

    @Resource
    private WeChatService weChatService;


    /**
     * 小程序登录
     *
     * @param code 小程序返回的code码
     * @return token
     */
    @GetMapping("getUserPhoneNumber")
    public Code2UserPhoneNumber getUserPhoneNumber(String code) {
        return weChatService.getUserPhoneNumber(code);
    }

    /**
     * 小程序登录
     *
     * @param code 小程序返回的code码
     * @return token
     */
    @GetMapping("jsCode2session")
    public Code2Session jsCode2session(String code) {
        return weChatService.jsCode2session(code);
    }
}
