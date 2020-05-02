package blog.develobeer.adminApi.utils;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Base64;

// 파일명 hash용도
public class SimpleAES256 {

    private static final String ALGORITHM_SPEC = "AES/CBC/PKCS5Padding";
    private static final String KEY_FACTORY = "PBKDF2WithHmacSHA1";
    private static final String AES = "AES";
    private static final int KEY_LENGTH = 256;

    private static final Base32 base32 = new Base32();

    public static String encryptAES256(String msg, String key, int iterationCount, boolean isBase32) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[20];
        random.nextBytes(saltBytes);

        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY);
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, iterationCount, KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), AES);

        Cipher cipher = Cipher.getInstance(ALGORITHM_SPEC);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();

        byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
        byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];

        System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);

        if (isBase32) {
            return base32.encodeAsString(buffer);
        } else {
            return Base64.getEncoder().encodeToString(buffer);
        }
    }

    public static String decryptAES256(String msg, String key, int iterationCount, boolean isBase32) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_SPEC);
        ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(msg));

        byte[] saltBytes = new byte[20];
        buffer.get(saltBytes, 0, saltBytes.length);

        byte[] ivBytes = new byte[cipher.getBlockSize()];
        buffer.get(ivBytes, 0, ivBytes.length);

        byte[] encryoptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes.length];
        buffer.get(encryoptedTextBytes);

        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY);
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, iterationCount, KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), AES);

        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

        if (isBase32) {
            return new String(base32.decode(encryoptedTextBytes));
        } else {
            return new String(cipher.doFinal(encryoptedTextBytes));
        }
    }


}
