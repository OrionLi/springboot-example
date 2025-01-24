package io.github.orionli.springbootexample.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HexUtil工具类的单元测试
 *
 * @author OrionLi
 * @date 2025/01/24
 */
class HexUtilTest {

    @Test
    void encryptAndFormat_Success() {
        // 准备测试数据
        String plainPassword = "password123";

        // 执行测试
        String encryptedPassword = HexUtil.encryptAndFormat(plainPassword);

        // 验证结果
        assertNotNull(encryptedPassword, "加密后的密码不应为null");
        String[] parts = encryptedPassword.split("#");
        assertEquals(3, parts.length, "加密后的密码格式应为: salt#algorithm#hash");
        assertTrue(parts[1].matches("v[1-3]"), "加密算法版本应为v1、v2或v3");
        assertFalse(parts[2].isEmpty(), "加密后的哈希值不应为空");
    }

    @Test
    void verify_Success() {
        // 准备测试数据
        String plainPassword = "password123";
        String encryptedPassword = HexUtil.encryptAndFormat(plainPassword);

        // 执行测试并验证
        assertTrue(HexUtil.verify(encryptedPassword, plainPassword), "使用正确的密码应验证通过");
    }

    @Test
    void verify_Fail_WrongPassword() {
        // 准备测试数据
        String correctPassword = "password123";
        String wrongPassword = "wrongpassword";
        String encryptedPassword = HexUtil.encryptAndFormat(correctPassword);

        // 执行测试并验证
        assertFalse(HexUtil.verify(encryptedPassword, wrongPassword), "使用错误的密码应验证失败");
    }

    @Test
    void verify_Should_ThrowException_When_StoredPasswordFormatInvalid() {
        // 准备测试数据
        String invalidStoredPassword = "invalidformat";
        String plainPassword = "password123";

        // 执行测试并验证
        assertThrows(IllegalArgumentException.class,
                () -> HexUtil.verify(invalidStoredPassword, plainPassword),
                "无效的存储密码格式应抛出IllegalArgumentException");
    }

    @Test
    void verify_Should_ThrowException_When_AlgorithmUnsupported() {
        // 准备测试数据
        String unsupportedAlgorithm = "salt#v9#hash";
        String plainPassword = "password123";

        // 执行测试并验证
        assertThrows(UnsupportedOperationException.class,
                () -> HexUtil.verify(unsupportedAlgorithm, plainPassword),
                "不支持的加密算法应抛出UnsupportedOperationException");
    }

    @Test
    void encryptAndFormat_MultipleExecutions_ShouldGenerateDifferentResults() {
        // 准备测试数据
        String plainPassword = "password123";

        // 执行测试
        String firstEncryption = HexUtil.encryptAndFormat(plainPassword);
        String secondEncryption = HexUtil.encryptAndFormat(plainPassword);

        // 验证结果
        assertNotEquals(firstEncryption, secondEncryption, "相同密码的两次加密结果应该不同");
        assertTrue(HexUtil.verify(firstEncryption, plainPassword), "第一次加密结果应该可以验证");
        assertTrue(HexUtil.verify(secondEncryption, plainPassword), "第二次加密结果应该可以验证");
    }

    @Test
    void verify_ConsistencyTest() {
        // 准备测试数据
        String plainPassword = "password123";
        String encryptedPassword = HexUtil.encryptAndFormat(plainPassword);

        // 执行多次验证
        for (int i = 0; i < 100; i++) {
            assertTrue(HexUtil.verify(encryptedPassword, plainPassword),
                    "密码验证应该在多次执行中保持一致性");
        }
    }
}