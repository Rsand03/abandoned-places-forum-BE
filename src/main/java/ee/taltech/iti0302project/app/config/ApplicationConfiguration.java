package ee.taltech.iti0302project.app.config;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {

    @Value("${LOCAL_JWT_SECRET_KEY:#{null}}")
    private String localJwtSecretKey;

    @Bean
    public SecretKey jwtKey() throws NoSuchAlgorithmException {
        // Jwt tokens become invalid after each backend restart - deployed to server
        if (localJwtSecretKey == null || localJwtSecretKey.isEmpty()) {
            return Jwts.SIG.HS256.key().build();
        }

        // Jwt tokens remain valid even after restarting backend - local testing
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedKey = digest.digest(localJwtSecretKey.getBytes(StandardCharsets.UTF_8));

        return new SecretKeySpec(hashedKey, "HmacSHA256");
    }
}
