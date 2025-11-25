package cn.smallyoung.springbootdemo.component;

import cn.hutool.core.lang.Dict;
import cn.smallyoung.springbootdemo.exception.BizException;
import cn.smallyoung.springbootdemo.interfaces.ResponseSysResult;
import cn.smallyoung.springbootdemo.util.SysResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通用信息返回和异常处理
 * @author smallyoung
 */
@Slf4j
@RestControllerAdvice
public class ResponseSysResultAdvice implements ResponseBodyAdvice<Object> {

    private static final Class<? extends Annotation> ANNOTATION_TYPE = ResponseSysResult.class;

    @Value("${spring.application.name:''}")
    private String applicationName;

    @Override
    public boolean supports(MethodParameter returnType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ANNOTATION_TYPE) || returnType.hasMethodAnnotation(ANNOTATION_TYPE);
    }

    @Override
    @SneakyThrows
    public Object beforeBodyWrite(Object body, @Nullable MethodParameter returnType, @Nullable MediaType selectedContentType,
                                  @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response) {
        if (body instanceof String) {
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(SysResult.success(body));
        }
        if (returnType != null && returnType.getMethod() != null
                && "java.lang.String".equals(returnType.getMethod().getReturnType().getName())) {
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(SysResult.success(body));
        }
        if (body instanceof SysResult) {
            return body;
        }
        return SysResult.success(body);
    }

    /**
     * 异常处理
     */
    @ExceptionHandler(value = Exception.class)
    public Map<String, Object> handler(HttpServletRequest request, Exception e) {
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (e instanceof BizException bizException && bizException.getCode() != null) {
            code = bizException.getCode();
        }
        return Dict.create().set("code", code).set("message", e.getMessage())
                .set("path", request.getRequestURI())
                .set("source", applicationName).set("method", request.getMethod())
                .set("timestamp", LocalDateTime.now());
    }

}
