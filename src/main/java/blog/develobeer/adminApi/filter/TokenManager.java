package blog.develobeer.adminApi.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Deprecated
public class TokenManager {
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    TokenManager(@Value("${key.private-key}") String privateKey, @Value("${key.public-key}") String publicKey) {
        this.setPrivateKey(privateKey);
        this.setPublicKey(publicKey);
    }

    private void setPrivateKey(String privateKey) {
        try {
            String privateKeyContent = privateKey.replaceAll("\\n", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace(" ", "");

            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            TokenManager.privateKey = keyFactory.generatePrivate(keySpecPKCS8);
        } catch (Exception e) {
            e.printStackTrace();
            TokenManager.privateKey = null;
        }
    }

    private void setPublicKey(String publicKey) {
        try {
            String publicKeyContent = publicKey.replaceAll("\\n", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace(" ", "");

            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            TokenManager.publicKey = keyFactory.generatePublic(keySpecX509);
        } catch (Exception e) {
            e.printStackTrace();
            TokenManager.publicKey = null;
        }
    }

    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] bytePlain = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytePlain);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText != null) {
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);

                byte[] byteEncrypted = Base64.getDecoder().decode(encryptedText.getBytes(StandardCharsets.UTF_8));
                byte[] bytePlain = cipher.doFinal(byteEncrypted);
                return new String(bytePlain, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
