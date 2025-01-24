package io.github.orionli.springbootexample.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author OrionLi
 */
@Data
public class LoginVO {

    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;

}
