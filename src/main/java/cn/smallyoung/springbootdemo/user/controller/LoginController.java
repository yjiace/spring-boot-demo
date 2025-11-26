package cn.smallyoung.springbootdemo.user.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import cn.smallyoung.springbootdemo.user.pojo.LoginByUsernameRequest;
import cn.smallyoung.springbootdemo.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author smallyoung
 */
@Slf4j
@RestController
@ResponseSysResult
@RequestMapping
public class LoginController {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取验证码
     */
    @GetMapping("captcha")
    public void captcha(HttpServletResponse response) {
        RandomGenerator randomGenerator = new RandomGenerator("23456789", 4);
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
        lineCaptcha.setGenerator(randomGenerator);
        lineCaptcha.createCode();
        log.info("生成的验证码：{}", lineCaptcha.getCode());
        String id = IdUtil.objectId();
        redisTemplate.opsForValue().set(getRedisKey(id), lineCaptcha.getCode(), 15, TimeUnit.MINUTES);
        try {
            //设置response响应
            response.setHeader("id", id);
            response.setHeader("Access-Control-Expose-Headers", "id");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");

            ServletOutputStream outputStream = response.getOutputStream();
            lineCaptcha.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new BizException("验证码生成错误：{}", e.getMessage());
        }
    }


    /**
     * 账号密码登录
     *
     * @return 登录需要的必要信息
     */
    @PostMapping("login/username")
    public Dict loginByUsername(@RequestBody LoginByUsernameRequest request) {
        if (StrUtil.hasBlank(request.getUsername(), request.getPassword(),
                request.getCode(), request.getCodeId())) {
            throw new BizException("缺少必要参数！");
        }
        String key = getRedisKey(request.getCodeId());
        if (!request.getCode().equalsIgnoreCase(redisTemplate.opsForValue().get(key) + "")) {
            redisTemplate.delete(key);
            throw new BizException("验证码错误！");
        }
        redisTemplate.delete(key);
        return userService.loginByUsername(request.getUsername(), request.getPassword(), request.getRemember());
    }


    /**
     * 验证码id生成规则
     *
     * @param id 验证码id
     * @return 生成后redis的key
     */
    private String getRedisKey(String id) {
        return "user_captcha_key:" + id;
    }
}
