package io.github.orionli.springbootexample.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.orionli.springbootexample.dao.UserDao;
import io.github.orionli.springbootexample.domain.User;
import io.github.orionli.springbootexample.service.UserService;
import io.github.orionli.springbootexample.util.Converter;
import io.github.orionli.springbootexample.vo.req.LoginVO;
import io.github.orionli.springbootexample.vo.req.UserRegisterVO;
import io.github.orionli.springbootexample.vo.req.UserUpdateVO;
import io.github.orionli.springbootexample.vo.resp.Result;
import io.github.orionli.springbootexample.vo.resp.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

/**
 * @author OrionLi
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserDao userDao;

    private final Converter converter;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> registerUser(@RequestBody @Valid UserRegisterVO userRegisterVO) {
        User user = converter.toUser(userRegisterVO);
        userService.registerUser(user);
        return Result.success("注册成功");

    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> loginUser(@RequestBody @Valid LoginVO loginVO) {
        Long id = userService.verifyPassword(loginVO.getUsername(), loginVO.getPassword());
        if (id == null) {
            return Result.fail("用户名或密码错误");
        }

        StpUtil.login(id);
        return Result.success("登录成功");
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("登出成功");
    }

    /**
     * 获取用户信息
     *
     * @return 成功则用户VO，不含敏感信息
     */
    @Operation(summary = "获取当前登录用户信息")
    @PostMapping("/info")
    public Result<UserVO> getUserInfo() {
        User userInfo = userService.getUserInfo(StpUtil.getLoginIdAsLong());
        return userInfo == null
                ? Result.fail("未查询到用户信息")
                : Result.success(converter.toUserVO(userInfo));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public Result<String> updateUserInfo(@RequestBody @Valid UserUpdateVO userUpdateVO) {
        User user = converter.toUser(userUpdateVO);
        user.setId(StpUtil.getLoginIdAsLong());

        userDao.updateById(user);
        return Result.success("更新成功");
    }

}
