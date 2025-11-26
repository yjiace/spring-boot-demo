package cn.smallyoung.springbootdemo.filter;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpStatus;
import cn.hutool.jwt.Claims;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.smallyoung.springbootdemo.config.JwtConfig;
import cn.smallyoung.springbootdemo.util.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * JWT鉴权过滤器
 *
 * @author smallyoung
 */
@Slf4j
@Configuration
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final AntPathMatcher MATCHER = new AntPathMatcher();
    @Resource
    private JwtConfig jwtConfig;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!jwtConfig.getEnable()) {
            log.info("鉴权系统关闭，鉴权放行");
            chain.doFilter(request, response);
            return;
        }
        String token = request.getHeader(jwtConfig.getTokenHead());
        if(StrUtil.isNotBlank(token)){
            token = token.replace("Bearer ", "");
        }
        //获取请求路径
        String uri = request.getRequestURI();
        //匿名访问
        if (jwtConfig.getAnonUrl().stream().anyMatch(pattern -> MATCHER.match(pattern, uri))) {
            log.info("鉴权放行：{}", uri);
            if (StrUtil.isNotBlank(token)) {
                try {
                    JWT jwt = JWT.of(token);
                    Claims claims = jwt.getPayload();
                    //用户id
                    String userId = claims.getClaimsJson().getStr(jwtConfig.getUserName());
                    request.setAttribute("userId", userId);
                } catch (Exception e) {
                    log.error("token验证失败：{}", e.getMessage());
                }
            }
            chain.doFilter(request, response);
            return;
        }
        //获取token
        log.info("通过路径：{}，获取token：{}", uri, token);
        if (StrUtil.isBlank(token)) {
            requestJson(request, response, HttpStatus.HTTP_UNAUTHORIZED, uri, "未获取到服务器端的令牌信息，请重新登录!", request.getMethod());
            return;
        }
        //验证签名
        RSA publicKey = new RSA(null, jwtConfig.getPublicKey());
        try {
            JWTValidator.of(token).validateAlgorithm(JWTSignerUtil.rs256(publicKey.getPublicKey()));
        } catch (Exception e) {
            requestJson(request, response, HttpStatus.HTTP_UNAUTHORIZED, uri, "解析验证令牌失败，请重新登录!", request.getMethod());
            return;
        }
        //校验时间
        JWT jwt = JWT.of(token);
        Claims claims = jwt.getPayload();
        String userId = claims.getClaimsJson().getStr(jwtConfig.getUserName());
        request.setAttribute("userId", userId);
        //判断token是否过期，如果未过期则重新签发token
        Date expiredDate = claims.getClaimsJson().getDate(JWTPayload.EXPIRES_AT);
        if (expiredDate != null && expiredDate.before(new Date())) {
            //如果是刷新接口，则放行
            if (expiredDate.before(DateUtil.offset(new Date(), DateField.MINUTE, jwtConfig.getRefreshTime()))) {
                requestJson(request, response, HttpStatus.HTTP_UNAUTHORIZED, uri, "令牌已过期失效!", request.getMethod());
                return;
            }
            if (!"/user/refresh".equals(uri)) {
                requestJson(request, response, HttpStatus.HTTP_PAYMENT_REQUIRED, uri, "令牌已过期失效,请尝试刷新token!", request.getMethod());
                return;
            }
        }
        //如果是单点登录，则优先与redis匹配
        String redisKey = UserUtil.getLoginRedisKey(userId, token);
        Object obj = redisTemplate.opsForValue().get(redisKey);
        List<String> authorities = new ArrayList<>();
        if (obj instanceof Dict dict) {
            if (!Objects.equals(token, dict.getStr(jwtConfig.getTokenHead()))) {
                requestJson(request, response, HttpStatus.HTTP_UNAUTHORIZED, uri, "令牌已失效，请重新登录!", request.getMethod());
                return;
            }
            String str = dict.getStr(jwtConfig.getAuthorityName());
            if (str != null) {
                authorities = Arrays.stream(str.split(",")).toList();
            }
        } else {
            requestJson(request, response, HttpStatus.HTTP_UNAUTHORIZED, uri, "未获取到服务器端的令牌信息，请重新登录!", request.getMethod());
            return;
        }
        //根据路径鉴权
        if (jwtConfig.getAuthUrl().stream().noneMatch(url -> MATCHER.match(url, uri)) && authorities.stream().noneMatch(pattern -> MATCHER.match(pattern, uri))) {
            requestJson(request, response, HttpStatus.HTTP_FORBIDDEN, uri, "您暂无此操作权限!", request.getMethod());
            return;
        }
        //判断是否禁止外网访问接口
        if (jwtConfig.getNoAccess().stream().anyMatch(pattern -> MATCHER.match(pattern, uri))) {
            requestJson(request, response, HttpStatus.HTTP_FORBIDDEN, uri, "本接口禁止外部服务访问!", request.getMethod());
            return;
        }
        chain.doFilter(request, response);
    }

    private void requestJson(HttpServletRequest request, HttpServletResponse response, Integer code, String uri, String message, String method) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            response.getWriter().write(StrUtil.format("""
                    {
                        "code": {},
                        "path": "{}",
                        "message": "{}",
                        "method": "{}",
                        "timestamp": "{}"
                    }
                    """, code, uri, message, method, LocalDateTime.now()));
        } catch (IOException e) {
            log.error("返回结果错误：{}", e.getMessage());
        }
    }
}
