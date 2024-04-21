package io.github.orionli.springbootexample.exception;

import io.github.orionli.springbootexample.enums.ResponseEnum;
import java.io.Serial;
import lombok.Getter;

/**
 * @author OrionLi
 */
@Getter
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1134867555484707611L;

    /**
     * 错误码
     */
    private String code = ResponseEnum.ERROR_400.getCode();

    /**
     * 响应枚举
     */
    private ResponseEnum responseEnum = ResponseEnum.ERROR_400;

    /**
     * 构造一个没有错误信息的 <code>BizException</code>
     */
    public BizException() {
        super();
    }

    /**
     * 使用指定的 Throwable 和 Throwable.toString() 作为异常信息来构造 BizException
     *
     * @param cause 错误原因， 通过 Throwable.getCause() 方法可以获取传入的 cause信息
     */
    public BizException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用错误信息 message 构造 BizException
     *
     * @param message 错误信息
     */
    public BizException(String message) {
        super(message);
    }

    /**
     * 使用错误码和错误信息构造 BizException
     *
     * @param code 错误码
     * @param message 错误信息
     */
    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用错误信息和 Throwable 构造 BizException
     *
     * @param message 错误信息
     * @param cause 错误原因
     */
    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param code 错误码
     * @param message 错误信息
     * @param cause 错误原因
     */
    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * @param responseEnum ErrorCodeEnum
     */
    public BizException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        code = responseEnum.getCode();
        this.responseEnum = responseEnum;
    }

    /**
     * @param responseEnum ErrorCodeEnum
     * @param cause 错误原因
     */
    public BizException(ResponseEnum responseEnum, Throwable cause) {
        super(responseEnum.getMessage(), cause);
        code = responseEnum.getCode();
        this.responseEnum = responseEnum;
    }

    /**
     * @param responseEnum ErrorCodeEnum
     * @param message 错误信息
     */
    public BizException(ResponseEnum responseEnum, String message) {
        super(message);
        code = responseEnum.getCode();
        this.responseEnum = responseEnum;
    }

}
