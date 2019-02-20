package blog.develobeer.adminApi.filter;

import org.springframework.beans.factory.annotation.Autowired;
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
public class TokenManager {
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    @Autowired
    public void setPrivateKey(@Value("${key.private-key}") String privateKey){
        try{
            String privateKeyContent = privateKey.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");

            System.out.println("###########################");
            System.out.println(privateKeyContent);

            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(keySpecPKCS8);
        }
        catch(Exception e){
            e.printStackTrace();
            this.privateKey = null;
        }
    }

    @Autowired
    public void setPublicKey(@Value("${key.public-key}") String publicKey){
        try{
            String publicKeyContent = publicKey.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");;

            System.out.println("###########################");
            System.out.println(publicKeyContent);

            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(keySpecX509);
        }
        catch(Exception e){
            e.printStackTrace();
            this.publicKey = null;
        }
    }

    public static String encrypt(String plainText){
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] bytePlain = cipher.doFinal(plainText.getBytes());

            String encrypted = Base64.getEncoder().encodeToString(bytePlain);

            return encrypted;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String encryptedText){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            byte[] byteEncrypted = Base64.getDecoder().decode(encryptedText.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] bytePlain = cipher.doFinal(byteEncrypted);

            String decrypted = new String(bytePlain, "utf-8");

            return decrypted;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
