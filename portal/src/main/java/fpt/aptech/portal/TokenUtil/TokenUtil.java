package fpt.aptech.portal.TokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;

public class TokenUtil {

    private static final String SECRET_KEY = "qwertyuiopasdfghjklzxcvbnm";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    
    public static String generateToken(Integer userId, Integer roleId) {
        byte[] apiKeySecretBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
                .claim("userId", userId)
                .claim("roleId", roleId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(signingKey)
                .compact();
    }

    public static Integer getUserIdFromToken(String token) {
        byte[] apiKeySecretBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Integer.class);
    }

    public static Integer getRoleIdFromToken(String token) {
        byte[] apiKeySecretBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("roleId", Integer.class);
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
