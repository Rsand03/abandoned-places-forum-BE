package ee.taltech.iti0302project.app.service.auth;

import ee.taltech.iti0302project.app.dto.auth.AuthResponseDto;
import ee.taltech.iti0302project.app.exception.ForbiddenException;
import ee.taltech.iti0302project.app.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class JwtService {

    private static final int JWT_EXPIRATION_TIME_MILLISECONDS = 1000 * 60 * 60 * 24;

    private final SecretKey key;


    public String generateJwt(AuthResponseDto user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of(
                        "userId", user.getUserId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "points", user.getPoints()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME_MILLISECONDS))
                .signWith(key)
                .compact();
    }

    public UUID extractUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);  // Remove "Bearer " prefix

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return UUID.fromString(claims.get("userId", String.class));
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    public void validateUserIdAgainstJwtUserId(String authHeader, UUID userId) {
        UUID tokenUserId = extractUserIdFromAuthHeader(authHeader);

        if (!userId.equals(tokenUserId)) {
            throw new ForbiddenException("UserId does not match userId in auth header jwt");
        }
    }

}
