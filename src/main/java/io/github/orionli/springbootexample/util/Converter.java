package io.github.orionli.springbootexample.util;

import io.github.orionli.springbootexample.domain.User;
import io.github.orionli.springbootexample.vo.req.UserRegisterVO;
import io.github.orionli.springbootexample.vo.req.UserUpdateVO;
import io.github.orionli.springbootexample.vo.resp.UserVO;
import org.mapstruct.Mapper;

/**
 * @author OrionLi
 */
@Mapper(componentModel = "spring")
public interface Converter {

    /**
     * userUpdateVO 转换成 User
     *
     * @param userUpdateVO 用户更新vo
     * @return 用户
     */
    User toUser(UserUpdateVO userUpdateVO);

    /**
     * userRegisterVO 转换成 User
     *
     * @param userRegisterVO 用户注册vo
     * @return 用户
     */
    User toUser(UserRegisterVO userRegisterVO);

    /**
     * user 转换成 UserVO
     *
     * @param user 用户
     * @return 用户vo，不含敏感信息
     */
    UserVO toUserVO(User user);

}
