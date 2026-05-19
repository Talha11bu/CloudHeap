package com.talha11bu.silkroad.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Service for generating and validating JSON Web Tokens (JWT).
 *
 * <p>Tokens encode the user's display name as the subject and the session ID
 * as a custom claim. They are signed with HMAC-SHA256 using a configurable
 * Base64-encoded secret key and expire after a configurable duration.</p>
 *
 * <p>Used by all authenticated endpoints and the WebSocket handshake interceptor
 * to verify user identity.</p>
 *
 * @see com.talha11bu.silkroad.config.JwtHandshakeInterceptor
 */
@Service
public class JwtTokenService {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.expiration-ms:8640000}")
	private long expirationMs;

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Generates a signed JWT token used to authenticate users during a session.
	 * The token encodes the user's display name as the subject and attaches the active session ID as a claim.
	 *
	 * @param sessionId   The ID of the session the user joined or created.
	 * @param displayName The chosen username of the user.
	 * @return A signed JWT string valid for the configured expiration duration.
	 */
	public String generateToken(String sessionId, String displayName) {
		return Jwts.builder()
                .setSubject(displayName)
                .claim("sessionId", sessionId)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationMs))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Validates the signature of the provided JWT token and parses its claims.
	 * If the token is expired or tampered with, this will throw an exception (handled securely upstream).
	 *
	 * @param token The JWT string to validate.
	 * @return The parsed claims (containing subject, sessionId, issue date, etc).
	 */
	public Claims validateAndParseToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
				.getBody();
	}
}
