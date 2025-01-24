package io.github.orionli.springbootexample.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.orionli.springbootexample.dao.UserDao;
import io.github.orionli.springbootexample.domain.User;
import io.github.orionli.springbootexample.enums.ResponseEnum;
import io.github.orionli.springbootexample.exception.BizException;
import io.github.orionli.springbootexample.service.UserService;
import io.github.orionli.springbootexample.util.Converter;
import io.github.orionli.springbootexample.vo.req.LoginVO;
import io.github.orionli.springbootexample.vo.req.UserRegisterVO;
import io.github.orionli.springbootexample.vo.req.UserUpdateVO;
import io.github.orionli.springbootexample.vo.resp.UserVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDao userDao;

    @MockitoBean
    private Converter converter;

    private MockedStatic<StpUtil> stpUtilMockedStatic;

    @BeforeEach
    void setUp() {
        stpUtilMockedStatic = Mockito.mockStatic(StpUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (stpUtilMockedStatic != null) {
            stpUtilMockedStatic.close();
        }
    }

    @Test
    void register_Success() throws Exception {
        // 准备测试数据
        UserRegisterVO registerVO = new UserRegisterVO();
        registerVO.setUsername("testUser");
        registerVO.setPassword("password123");
        registerVO.setPhoneNumber("13800138000");
        registerVO.setSex("O"); // 非M/F的其他值

        User user = new User();
        when(converter.toUser(any(UserRegisterVO.class))).thenReturn(user);

        // 执行测试
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.SUCCESS.getCode()))
                .andExpect(jsonPath("$.msg").value("注册成功"));
    }

    @Test
    void register_Fail_When_Username_Exists() throws Exception {
        // 准备测试数据
        UserRegisterVO registerVO = new UserRegisterVO();
        registerVO.setUsername("existingUser");
        registerVO.setPassword("password123");
        registerVO.setPhoneNumber("13800138000");
        registerVO.setSex("O");

        User user = new User();
        when(converter.toUser(any(UserRegisterVO.class))).thenReturn(user);
        doThrow(new BizException(ResponseEnum.USERNAME_IS_EXIST))
                .when(userService).register(any(User.class));

        // 执行测试
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.USERNAME_IS_EXIST.getCode()))
                .andExpect(jsonPath("$.msg").value(ResponseEnum.USERNAME_IS_EXIST.getMessage()));
    }

    @Test
    void register_Fail_When_Invalid_Sex() throws Exception {
        // 准备测试数据
        UserRegisterVO registerVO = new UserRegisterVO();
        registerVO.setUsername("testUser");
        registerVO.setPassword("password123");
        registerVO.setPhoneNumber("13800138000");
        registerVO.setSex("M"); // 使用M作为性别

        User user = new User();
        user.setSex("M");
        when(converter.toUser(any(UserRegisterVO.class))).thenReturn(user);

        // 执行测试
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.SEX_ERROR.getCode()))
                .andExpect(jsonPath("$.msg").value(ResponseEnum.SEX_ERROR.getMessage()));
    }

    @Test
    void register_Fail_When_Username_Empty() throws Exception {
        // 准备测试数据
        UserRegisterVO registerVO = new UserRegisterVO();
        registerVO.setUsername("");
        registerVO.setPassword("password123");
        registerVO.setPhoneNumber("13800138000");
        registerVO.setSex("O");

        // 执行测试
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.CLIENT_BAD_PARAMETERS.getCode()));
    }

    @Test
    void register_Fail_When_Password_Too_Short() throws Exception {
        // 准备测试数据
        UserRegisterVO registerVO = new UserRegisterVO();
        registerVO.setUsername("testUser");
        registerVO.setPassword("123"); // 密码太短
        registerVO.setPhoneNumber("13800138000");
        registerVO.setSex("O");

        // 执行测试
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.CLIENT_BAD_PARAMETERS.getCode()));
    }

    @Test
    void login_Fail_When_Username_Empty() throws Exception {
        // 准备测试数据
        LoginVO loginVO = new LoginVO();
        loginVO.setUsername("");
        loginVO.setPassword("password123");

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.CLIENT_BAD_PARAMETERS.getCode()));
    }

    @Test
    void login_Fail_When_Password_Empty() throws Exception {
        // 准备测试数据
        LoginVO loginVO = new LoginVO();
        loginVO.setUsername("testUser");
        loginVO.setPassword("");

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.CLIENT_BAD_PARAMETERS.getCode()));
    }

    @Test
    void login_Success() throws Exception {
        // 准备测试数据
        LoginVO loginVO = new LoginVO();
        loginVO.setUsername("testUser");
        loginVO.setPassword("password123");

        when(userService.verifyPassword(loginVO.getUsername(), loginVO.getPassword()))
                .thenReturn(1L);

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.SUCCESS.getCode()))
                .andExpect(jsonPath("$.msg").value("登录成功"));
    }

    @Test
    void login_Fail() throws Exception {
        // 准备测试数据
        LoginVO loginVO = new LoginVO();
        loginVO.setUsername("testUser");
        loginVO.setPassword("wrongPassword");

        when(userService.verifyPassword(loginVO.getUsername(), loginVO.getPassword()))
                .thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getCode()))
                .andExpect(jsonPath("$.msg").value(ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getMessage()));
    }

    @Test
    void logout_Success() throws Exception {
        // 执行测试
        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.SUCCESS.getCode()))
                .andExpect(jsonPath("$.msg").value("登出成功"));
    }

    @Test
    void getUserInfo_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        User user = new User();
        UserVO userVO = new UserVO();

        stpUtilMockedStatic.when(StpUtil::getLoginIdAsLong).thenReturn(userId);
        when(userService.getUserInfo(userId)).thenReturn(user);
        when(converter.toUserVO(user)).thenReturn(userVO);

        // 执行测试
        mockMvc.perform(post("/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.SUCCESS.getCode()));
    }

    @Test
    void getUserInfo_Fail_When_UserNotFound() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        stpUtilMockedStatic.when(StpUtil::getLoginIdAsLong).thenReturn(userId);
        when(userService.getUserInfo(userId)).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.FAIL.getCode()))
                .andExpect(jsonPath("$.msg").value("未查询到用户信息"));
    }

    @Test
    void getUserInfo_Fail_When_NotLoggedIn() throws Exception {
        // 准备测试数据
        stpUtilMockedStatic.when(StpUtil::getLoginIdAsLong).thenThrow(new NotLoginException("", "", ""));

        // 执行测试
        mockMvc.perform(post("/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.NO_LOGIN.getCode()))
                .andExpect(jsonPath("$.msg").value(ResponseEnum.NO_LOGIN.getMessage()));
    }

    @Test
    void updateUserInfo_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        UserUpdateVO updateVO = new UserUpdateVO();
        updateVO.setUsername("updatedUser");
        updateVO.setPhoneNumber("13900139000");
        updateVO.setSex("O");

        User user = new User();
        stpUtilMockedStatic.when(StpUtil::getLoginIdAsLong).thenReturn(userId);
        when(converter.toUser(any(UserUpdateVO.class))).thenReturn(user);

        // 执行测试
        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.SUCCESS.getCode()))
                .andExpect(jsonPath("$.msg").value("更新成功"));
    }

    @Test
    void updateUserInfo_Fail_When_NotLoggedIn() throws Exception {
        // 准备测试数据
        UserUpdateVO updateVO = new UserUpdateVO();
        updateVO.setUsername("updatedUser");
        updateVO.setPhoneNumber("13900139000");
        updateVO.setSex("O");

        stpUtilMockedStatic.when(StpUtil::getLoginIdAsLong).thenThrow(new NotLoginException("", "", ""));

        // 执行测试
        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.NO_LOGIN.getCode()))
                .andExpect(jsonPath("$.msg").value(ResponseEnum.NO_LOGIN.getMessage()));
    }

    @Test
    void updateUserInfo_Fail_When_DuplicateUsername() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        UserUpdateVO updateVO = new UserUpdateVO();
        updateVO.setUsername("existingUser");
        updateVO.setPhoneNumber("13900139000");
        updateVO.setSex("O");

        User user = new User();
        stpUtilMockedStatic.when(StpUtil::getLoginIdAsLong).thenReturn(userId);
        when(converter.toUser(any(UserUpdateVO.class))).thenReturn(user);
        when(userDao.updateById(any(User.class))).thenThrow(new DuplicateKeyException("Duplicate entry"));

        // 执行测试
        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseEnum.ERROR_500.getCode()));
    }
}