package io.github.orionli.encryption;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import jakarta.validation.constraints.NotBlank;

/**
 * 哈希加密和比对工具
 * <p>
 * 用于密码的加密和验证，使用指定的加密算法，使用动态盐值
 * <p>
 * 如果不想被入侵者通过哈希长度判断算法类型，也可以对哈希进行统一长度裁剪
 * <p>
 * 当然，也可以混合使用多种加密算法
 *
 * @author OrionLi
 * @date 2024/02/11
 */
public final class HexUtil {

    /**
     * 分隔符
     */
    private static final String SEPARATOR = "#";

    /**
     * 分割 {@link HexUtil#SEPARATOR} 后的长度
     */
    private static final int AFTER_SEPARATOR_LENGTH = 3;

    /**
     * sm3摘要算法
     */
    private static final Digester SM3_DIGESTER = DigestUtil.digester("sm3");

    /**
     * 加密策略工厂，存储已知的加密策略实例
     */
    private static final Map<String, EncryptionPolicy> ENCRYPTION_POLICY_MAP = Map.of(
            "v1", (plainPassword, salt) -> SecureUtil.md5(plainPassword + salt),
            "v2", (plainPassword, salt) -> SecureUtil.sha1(plainPassword + salt),
            "v3", ((plainPassword, salt) -> SM3_DIGESTER.digestHex(plainPassword + salt))
    );

    private static final List<String> POLICY_CODES = ENCRYPTION_POLICY_MAP.keySet().stream().toList();

    /**
     * 用户登录时调用此方法，根据数据库中取出的password字段进行验证
     *
     * @param storedPassword 数据库中存储的密码字符串
     * @param plainPassword 用户输入的明文密码
     * @return 如果密码匹配，则返回true；否则返回false
     */
    @NotBlank
    public static boolean verify(String storedPassword, String plainPassword) {
        String[] parts = storedPassword.split(SEPARATOR);
        if (parts.length != AFTER_SEPARATOR_LENGTH) {
            throw new IllegalArgumentException("Invalid password format, storedPassword: " + storedPassword);
        }
        String salt = parts[0];
        String algorithmCode = parts[1];
        String encryptedValue = parts[2];

        EncryptionPolicy policy = ENCRYPTION_POLICY_MAP.get(algorithmCode);
        if (policy == null) {
            throw new UnsupportedOperationException("Unsupported encryption algorithm: " + algorithmCode);
        }

        return encryptedValue.equals(policy.encrypt(plainPassword, salt));
    }

    /**
     * 创建一个新的密码记录（用于用户注册）
     *
     * @param plainPassword 明文密码
     * @return 格式化后的加密密码字符串
     */
    @NotBlank
    public static String encryptAndFormat(String plainPassword) {
        String algorithmCode = POLICY_CODES.get(ThreadLocalRandom.current().nextInt(POLICY_CODES.size()));
        EncryptionPolicy policy = ENCRYPTION_POLICY_MAP.get(algorithmCode);

        String dynamicSalt = RandomUtil.randomString(ThreadLocalRandom.current().nextInt(4, 8));
        String encryptedValue = policy.encrypt(plainPassword, dynamicSalt);

        return dynamicSalt + SEPARATOR + algorithmCode + SEPARATOR + encryptedValue;
    }

    /**
     * 加密策略接口
     *
     * @author OrionLi
     */
    @FunctionalInterface
    interface EncryptionPolicy {

        /**
         * 加密给定的明文密码
         *
         * @param plainPassword 明文密码
         * @param salt 盐值
         * @return 加密后的密码字符串
         */
        String encrypt(String plainPassword, String salt);

    }

}

