package io.github.orionli.springbootexample.service.impl;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.util.MapUtils;
import io.github.orionli.springbootexample.dao.UserDao;
import io.github.orionli.springbootexample.domain.User;
import io.github.orionli.springbootexample.enums.ResponseEnum;
import io.github.orionli.springbootexample.exception.BizException;
import io.github.orionli.springbootexample.service.UserService;
import io.github.orionli.springbootexample.util.Converter;
import io.github.orionli.springbootexample.util.HexUtil;
import java.util.Objects;
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

    private final BeanSearcher beanSearcher;

    private final Converter converter;

    @Override
    public Long verifyPassword(String username, String password) {
        // 根据用户名查询用户信息
        User user = beanSearcher.searchFirst(
                User.class,
                MapUtils.builder()
                        .onlySelect(User::getId, User::getPassword)
                        .field(User::getUsername, username)
                        .limit(0, 1)
                        .build()
        );

        // 验证密码是否正确
        return user != null && HexUtil.verify(user.getPassword(), password)
                ? user.getId()
                : null;
    }

    @Override
    public Long registerUser(User user) {
        // 用户名长度校验
        if (user.getUsername().length() < 4) {
            throw new BizException(ResponseEnum.USERNAME_LENGTH_ERROR);
        }
        // 用户名格式校验
        if (user.getUsername().matches(".*[^a-zA-Z0-9_].*")) {
            throw new BizException(ResponseEnum.USERNAME_FORMAT_ERROR);
        }
        // 密码长度校验
        if (user.getPassword().length() < 6) {
            throw new BizException(ResponseEnum.PASSWORD_LENGTH_ERROR);
        }
        if (Objects.equals(user.getSex(), "F") || Objects.equals(user.getSex(), "M")) {
            throw new BizException(ResponseEnum.SEX_ERROR);
        }

        try {
            user.setId(User.generateId())
                    .setPassword(HexUtil.encryptAndFormat(user.getPassword()));
            userDao.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BizException(ResponseEnum.USERNAME_IS_EXIST);
        }
        return user.getId();
    }

    @Override
    public User getUserInfo(Long id) {
        return userDao.getUserById(id);
    }

}




