package cn.smallyoung.springbootdemo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author smallyoung
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3937755350175676600L;

    private Integer code;

    private String message;

    private T data;

    private SysResult() {
    }

    private SysResult(HttpStatus status, T data) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
        this.data = data;
    }

    private SysResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> SysResult<T> result(HttpStatus status, T data) {
        return new SysResult<>(status, data);
    }

    public static <T> SysResult<T> success(T data) {
        return result(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, data);
    }

    public static <T> SysResult<T> failure(String msg) {
        return new SysResult<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, null);
    }

}
