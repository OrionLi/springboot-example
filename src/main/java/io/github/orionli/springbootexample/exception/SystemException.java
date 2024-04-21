package io.github.orionli.springbootexample.exception;

import io.github.orionli.springbootexample.enums.ResponseEnum;
import java.io.Serial;
import lombok.Getter;

/**
 * 系统异常
 *
 * @author OrionLi
 * @date 2024/01/06
 */
@Getter
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3729438050537226652L;

    /**
     * 错误码
     */
    private String code = ResponseEnum.ERROR_500.getCode();

    /**
     * 响应枚举
     */
    private ResponseEnum responseEnum = ResponseEnum.ERROR_500;

    /**
     * 构造一个没有错误信息的 <code>SystemException</code>
     */
    public SystemException() {
        super();
    }

    /**
     * 使用指定的 Throwable 和 Throwable.toString() 作为异常信息来构造 SystemException
     *
     * @param cause 错误原因， 通过 Throwable.getCause() 方法可以获取传入的 cause信息
     */
    public SystemException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用错误信息 message 构造 SystemException
     *
     * @param message 错误信息
     */
    public SystemException(String message) {
        super(message);
    }

    /**
     * 使用错误码和错误信息构造 SystemException
     *
     * @param code 错误码
     * @param message 错误信息
     */
    public SystemException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用错误信息和 Throwable 构造 SystemException
     *
     * @param message 错误信息
     * @param cause 错误原因
     */
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param code 错误码
     * @param message 错误信息
     * @param cause 错误原因
     */
    public SystemException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * @param responseEnum ErrorCodeEnum
     */
    public SystemException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        code = responseEnum.getCode();
        this.responseEnum = responseEnum;
    }

    /**
     * @param responseEnum ErrorCodeEnum
     * @param cause 错误原因
     */
    public SystemException(ResponseEnum responseEnum, Throwable cause) {
        super(responseEnum.getMessage(), cause);
        code = responseEnum.getCode();
        this.responseEnum = responseEnum;
    }

    /**
     * @param responseEnum ErrorCodeEnum
     * @param message 错误信息
     */
    public SystemException(ResponseEnum responseEnum, String message) {
        super(message);
        code = responseEnum.getCode();
        this.responseEnum = responseEnum;
    }

}
