package io.github.orionli.springbootexample.vo;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * @author OrionLi
 */
@Data
public class UserRegisterVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7335293658918571816L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
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
