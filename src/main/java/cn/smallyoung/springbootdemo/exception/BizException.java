package cn.smallyoung.springbootdemo.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 统一异常处理器
 */
@Setter
@Getter
public class BizException extends RuntimeException {


    @Serial
    private static final long serialVersionUID = 6868285969639994913L;

    private String msg;

    private Integer code;

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BizException(String msg, Object... str) {
        super(StrUtil.format(msg, str));
        this.msg = StrUtil.format(msg, str);
    }

    public BizException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public BizException(String msg, Integer code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public BizException(String msg, Integer code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}
