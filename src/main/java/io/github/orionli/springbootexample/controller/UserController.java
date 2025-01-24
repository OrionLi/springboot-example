package io.github.orionli.springbootexample.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.orionli.springbootexample.dao.UserDao;
import io.github.orionli.springbootexample.domain.User;
import io.github.orionli.springbootexample.enums.ResponseEnum;
import io.github.orionli.springbootexample.exception.BizException;
import io.github.orionli.springbootexample.service.UserService;
import io.github.orionli.springbootexample.util.Converter;
import io.github.orionli.springbootexample.vo.req.LoginVO;
import io.github.orionli.springbootexample.vo.req.UserRegisterVO;
import io.github.orionli.springbootexample.vo.req.UserUpdateVO;
import io.github.orionli.springbootexample.vo.resp.Result;
import io.github.orionli.springbootexample.vo.resp.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器，处理用户相关的HTTP请求，包括注册、登录、登出、信息查询和更新等操作。
 *
 * @author OrionLi
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    public static final String MALE = "M";
    public static final String FEMALE = "F";
    private final UserService userService;
    private final UserDao userDao;
    private final Converter converter;

    /**
     * 处理用户注册请求。
     *
     * @param userRegisterVO 用户注册信息，包含用户名、密码、电话号码和性别等信息
     * @return 注册结果，成功返回成功信息
     * @throws BizException 当性别不符合要求时抛出业务异常
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid UserRegisterVO userRegisterVO) {
        User user = converter.toUser(userRegisterVO);

        if (FEMALE.equals(user.getSex()) || MALE.equals(user.getSex())) {
            throw new BizException(ResponseEnum.SEX_ERROR);
        }

        userService.register(user);
        return Result.success("注册成功");
    }

    /**
     * 处理用户登录请求。
     *
     * @param loginVO 登录信息，包含用户名和密码
     * @return 登录结果，成功则返回成功信息，失败则返回错误提示
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginVO loginVO) {
        Long id = userService.verifyPassword(loginVO.getUsername(), loginVO.getPassword());
        if (id == null) {
            return Result.fail(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        StpUtil.login(id);
        return Result.success("登录成功");
    }

    /**
     * 处理用户登出请求。
     *
     * @return 登出结果，返回成功信息
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("登出成功");
    }

    /**
     * 获取当前登录用户的信息。
     *
     * @return 用户信息结果，成功返回不含敏感信息的用户VO，失败返回错误信息
     * @see UserVO 用户信息视图对象，不包含敏感信息
     */
    @PostMapping("/info")
    public Result<UserVO> getUserInfo() {
        User userInfo = userService.getUserInfo(StpUtil.getLoginIdAsLong());
        return userInfo == null
                ? Result.fail("未查询到用户信息")
                : Result.success(converter.toUserVO(userInfo));
    }

    /**
     * 更新当前登录用户的信息。
     *
     * @param userUpdateVO 用户更新信息，包含需要更新的用户信息字段
     * @return 更新结果，成功返回成功信息
     */
    @PutMapping("/update")
    public Result<String> updateUserInfo(@RequestBody @Valid UserUpdateVO userUpdateVO) {
        User user = converter.toUser(userUpdateVO);
        user.setId(StpUtil.getLoginIdAsLong());

        if (FEMALE.equals(user.getSex()) || MALE.equals(user.getSex())) {
            throw new BizException(ResponseEnum.SEX_ERROR);
        }

        userDao.updateById(user);
        return Result.success("更新成功");
    }

}
