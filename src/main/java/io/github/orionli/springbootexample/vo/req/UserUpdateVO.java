package io.github.orionli.springbootexample.vo.req;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * @author OrionLi
 */
@Data
public class UserUpdateVO implements Serializable {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

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

    @Serial
    private static final long serialVersionUID = -1868367016307170342L;

}
