package io.github.orionli.springbootexample.service;

import io.github.orionli.springbootexample.domain.User;

/**
 * 用户服务
 *
 * @author OrionLi
 * @date 2024/04/21
 */
public interface UserService {

    /**
     * 验证密码
     *
     * @param username 用户名
     * @param password 密码
     * @return id
     */
    Long verifyPassword(String username, String password);

    /**
     * 注册用户
     *
     * @param user 用户
     * @return id
     */
    Long registerUser(User user);

    /**
     * 获取用户信息
     *
     * @param id id
     * @return 用户
     */
    User getUserInfo(Long id);

}
