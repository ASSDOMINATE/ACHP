package org.dominate.achp.common.utils;


import com.hwja.tool.utils.RandomUtil;

/**
 * 密码工具
 *
 * @author dominate
 * @since 2022/01/04
 */
public final class PasswordUtil {

    private static final int SALT_NUM_LENGTH = 16;

    private static final int INIT_PASSWORD_LENGTH = 12;

    /**
     * 加密密码
     *
     * @param password 密码
     * @param salt     盐值
     * @return 和盐值加密后的密码
     */
    public static String encryptPassword(String password, String salt) {
        return EncryptionUtil.encryptMd5(salt + password + salt);
    }

    /**
     * 验证密码
     *
     * @param password      密码
     * @param salt          盐值
     * @param checkPassword 需要检查的密码
     * @return 验证结果
     */
    public static boolean validPassword(String password, String salt, String checkPassword) {
        return checkPassword.equals(encryptPassword(password, salt));
    }

    /**
     * 生成随机盐值
     *
     * @return 16位随机字符串
     */
    public static String createSalt() {
        return RandomUtil.getStringRandom(SALT_NUM_LENGTH);
    }

    public static String createRandPassword() {
        return RandomUtil.getStringRandom(INIT_PASSWORD_LENGTH);
    }
}
