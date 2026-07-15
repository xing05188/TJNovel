package edu.tongji.userservice.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用PBKDF2算法，与原Backend保持一致
 */
public class PasswordHasher {
    
    private static final byte[] SALT = "FixedSalt123456".getBytes(StandardCharsets.UTF_8);
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 120; // 15 bytes = 120 bits
    
    /**
     * 加密密码
     * @param password 原始密码
     * @return 加密后的密码（Base64编码，截取前20个字符）
     */
    public static String hashPassword(String password) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                SALT,
                ITERATIONS,
                KEY_LENGTH
            );
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashBytes = factory.generateSecret(spec).getEncoded();
            
            // 取前15个字节，转换为Base64，再截取前20个字符
            byte[] truncatedHash = new byte[15];
            System.arraycopy(hashBytes, 0, truncatedHash, 0, 15);
            String base64Hash = Base64.getEncoder().encodeToString(truncatedHash);
            
            // 截取前20个字符，与原Backend保持一致
            return base64Hash.length() > 20 ? base64Hash.substring(0, 20) : base64Hash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证密码
     * @param password 原始密码
     * @param hashed 加密后的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String hashed) {
        String newHash = hashPassword(password);
        return newHash.equals(hashed);
    }
}

