package io.github.orionli.springbootexample.vo.resp;

import io.github.orionli.springbootexample.enums.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author OrionLi
 */
@Getter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public final class Result<T> {

    /**
     * 响应状态
     */
    private String code;

    /**
     * 响应编码
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(ResponseEnum responseEnum) {
        this(responseEnum, null);
    }

    public Result(ResponseEnum responseEnum, T data) {
        this(responseEnum, responseEnum.getMessage(), data);
    }

    public Result(ResponseEnum responseEnum, String msg, T data) {
        code = responseEnum.getCode();
        this.msg = msg;
        this.data = data;
    }

    /**
     * @return 默认成功响应
     */
    public static Result<Void> success() {
        return new Result<>(ResponseEnum.SUCCESS);
    }

    /**
     * 自定义信息的成功响应
     * <p>
     * 通常用作插入成功等并显示具体操作通知如: return ResultEnhance.success("发送信息成功")
     * </p>
     *
     * @param msg 信息
     * @return 自定义信息的成功响应
     */
    public static <T> Result<T> success(String msg) {
        return new Result<>(ResponseEnum.SUCCESS, msg, null);
    }

    /**
     * 带数据的成功响应
     *
     * @param data 数据
     * @return 带数据的成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResponseEnum.SUCCESS, data);
    }

    /**
     * @return 默认失败响应
     */
    public static Result<Void> fail() {
        return new Result<>(ResponseEnum.FAIL, ResponseEnum.FAIL.getMessage(), null);
    }

    /**
     * 自定义错误信息的失败响应
     *
     * @param msg 错误信息
     * @return 自定义错误信息的失败响应
     */
    public static <T> Result<T> fail(String msg) {
        return fail(ResponseEnum.FAIL, msg);
    }

    /**
     * 自定义状态的失败响应
     *
     * @param responseEnum 状态
     * @return 自定义状态的失败响应
     */
    public static <T> Result<T> fail(ResponseEnum responseEnum) {
        return fail(responseEnum, responseEnum.getMessage());
    }

    /**
     * 自定义状态和信息的失败响应
     *
     * @param responseEnum 状态
     * @param msg          信息
     * @return 自定义状态和信息的失败响应
     */
    public static <T> Result<T> fail(ResponseEnum responseEnum, String msg) {
        return new Result<>(responseEnum, msg, null);
    }

}
