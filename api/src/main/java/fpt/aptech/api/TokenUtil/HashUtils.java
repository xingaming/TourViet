package fpt.aptech.api.TokenUtil;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class HashUtils {

    public static String signSHA256(String rawHash, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);

        byte[] signatureBytes = hmacSHA256.doFinal(rawHash.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : signatureBytes) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
