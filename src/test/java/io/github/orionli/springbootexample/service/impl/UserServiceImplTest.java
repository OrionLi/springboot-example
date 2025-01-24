package io.github.orionli.springbootexample.service.impl;

import io.github.orionli.springbootexample.dao.UserDao;
import io.github.orionli.springbootexample.domain.User;
import io.github.orionli.springbootexample.enums.ResponseEnum;
import io.github.orionli.springbootexample.exception.BizException;
import io.github.orionli.springbootexample.util.HexUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L)
                .setUsername("testUser")
                .setPassword("password123")
                .setPhoneNumber("13800138000")
                .setSex("O");
    }

    @Test
    void verifyPassword_Success() {
        // 准备测试数据
        String username = "testUser";
        String password = "password123";
        String encryptedPassword = HexUtil.encryptAndFormat(password);
        User storedUser = new User();
        storedUser.setId(1L).setPassword(encryptedPassword);

        // 模拟DAO层行为
        when(userDao.getIdAndPasswordByUsername(username)).thenReturn(storedUser);

        // 执行测试
        Long result = userService.verifyPassword(username, password);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result);
        verify(userDao).getIdAndPasswordByUsername(username);
    }

    @Test
    void verifyPassword_Fail_WrongPassword() {
        // 准备测试数据
        String username = "testUser";
        String correctPassword = "password123";
        String wrongPassword = "wrongPassword";
        String encryptedPassword = HexUtil.encryptAndFormat(correctPassword);
        User storedUser = new User();
        storedUser.setId(1L).setPassword(encryptedPassword);

        // 模拟DAO层行为
        when(userDao.getIdAndPasswordByUsername(username)).thenReturn(storedUser);

        // 执行测试
        Long result = userService.verifyPassword(username, wrongPassword);

        // 验证结果
        assertNull(result);
        verify(userDao).getIdAndPasswordByUsername(username);
    }

    @Test
    void verifyPassword_Fail_UserNotFound() {
        // 准备测试数据
        String username = "nonexistentUser";
        String password = "password123";

        // 模拟DAO层行为
        when(userDao.getIdAndPasswordByUsername(username)).thenReturn(null);

        // 执行测试
        Long result = userService.verifyPassword(username, password);

        // 验证结果
        assertNull(result);
        verify(userDao).getIdAndPasswordByUsername(username);
    }

    @Test
    void register_Success() {
        // 准备测试数据
        when(userDao.insert(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.register(testUser));

        // 验证结果
        verify(userDao).insert(any(User.class));
        assertNotNull(testUser.getId());
        assertTrue(testUser.getPassword().contains("#"));
    }

    @Test
    void register_Fail_DuplicateUsername() {
        // 模拟DAO层抛出重复键异常
        when(userDao.insert(any(User.class))).thenThrow(new DuplicateKeyException("Duplicate entry"));

        // 执行测试并验证异常
        BizException exception = assertThrows(BizException.class, () -> userService.register(testUser));
        assertEquals(ResponseEnum.USERNAME_IS_EXIST, exception.getResponseEnum());

        // 验证交互
        verify(userDao).insert(any(User.class));
    }

    @Test
    void getUserInfo_Success() {
        // 准备测试数据
        Long userId = 1L;
        when(userDao.getUserById(userId)).thenReturn(testUser);

        // 执行测试
        User result = userService.getUserInfo(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userDao).getUserById(userId);
    }

    @Test
    void getUserInfo_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        when(userDao.getUserById(userId)).thenReturn(null);

        // 执行测试
        User result = userService.getUserInfo(userId);

        // 验证结果
        assertNull(result);
        verify(userDao).getUserById(userId);
    }
}