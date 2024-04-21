package io.github.orionli.springbootexample.enums;

import io.github.orionli.springbootexample.vo.resp.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 响应枚举
 *
 * @author OrionLi
 * @date 2024/01/13
 */
@Getter
@ToString
@AllArgsConstructor
public enum ResponseEnum {
    /**
     * 错误
     */
    ERROR_500("500", "服务器未知错误"),
    ERROR_400("400", "错误请求"),

    /**
     * {@link Result} 默认返回的类型
     * OK：操作成功
     */
    SUCCESS("0", "操作成功"),
    FAIL("-1", "操作失败"),

    /**
     * 客户端
     */
    CLIENT_BAD_PARAMETERS("A0001", "客户端参数错误"),
    NO_LOGIN("A0002", "还未登录，请先登录"),

    /**
     * 系统
     */
    SERVICE_ERROR("B0001", "服务执行异常"),
    RESOURCE_NOT_FOUND("B0404", "资源不存在"),
    DAO_ERROR("B0002", "数据库操作异常"),

    /**
     * 用户
     */
    USERNAME_IS_EXIST("C0001", "用户名已存在"),
    PASSWORD_LENGTH_ERROR("C0002", "密码长度不符合要求"),
    USERNAME_LENGTH_ERROR("C0003", "用户名长度不符合要求"),
    USERNAME_FORMAT_ERROR("C0004", "用户名格式不符合要求"),
    SEX_ERROR("C0005", "性别不符合要求");

    /**
     * 响应状态
     */
    private final String code;

    /**
     * 响应编码
     */
    private final String message;
}
