package io.github.orionli.springbootexample.vo;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * @author OrionLi
 */
@Data
public class LoginVO implements Serializable {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Serial
    private static final long serialVersionUID = -7539440756233860090L;

}
