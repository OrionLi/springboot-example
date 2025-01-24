package io.github.orionli.springbootexample.service.impl;

import io.github.orionli.springbootexample.dao.UserDao;
import io.github.orionli.springbootexample.domain.User;
import io.github.orionli.springbootexample.enums.ResponseEnum;
import io.github.orionli.springbootexample.exception.BizException;
import io.github.orionli.springbootexample.service.UserService;
import io.github.orionli.springbootexample.util.HexUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 *
 * @author OrionLi
 * @date 2024/04/21
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public Long verifyPassword(String username, String password) {
        // 根据用户名查询用户信息
        User user = userDao.getIdAndPasswordByUsername(username);

        // 验证密码是否正确
        return user != null && HexUtil.verify(user.getPassword(), password)
                ? user.getId()
                : null;
    }

    @Override
    public void register(User user) {
        try {
            user.setId(User.generateId())
                    .setPassword(HexUtil.encryptAndFormat(user.getPassword()));
            userDao.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BizException(ResponseEnum.USERNAME_IS_EXIST);
        }
    }

    @Override
    public User getUserInfo(Long id) {
        return userDao.getUserById(id);
    }

}




