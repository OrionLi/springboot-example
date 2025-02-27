package io.github.orionli.springbootexample.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author OrionLi
 */
@Data
public class UserRegisterVO {

    /**
     * 用户名
     * 用户名长度必须在4到20个字符之间
     * 用户名只能包含字母、数字和下划线
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     * 密码长度至少为6个字符
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 电话号码
     */
    @NotBlank(message = "电话号码不能为空")
    private String phoneNumber;

    /**
     * 性别
     */
    @NotBlank(message = "性别不能为空")
    private String sex;

}
