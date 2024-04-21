package io.github.orionli.springbootexample.vo.resp;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户vo，不含敏感信息
 *
 * @author OrionLi
 * @date 2024/04/21
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 性别
     */
    private String sex;

    @Serial
    private static final long serialVersionUID = 8016927999409434933L;

}
