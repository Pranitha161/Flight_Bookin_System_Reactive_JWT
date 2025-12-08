package com.flightapp.demo.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${security.jwt.expiration-minutes}")
	private long expirationMinutes;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public boolean validateToken(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration() != null && claims.getExpiration().after(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	public String extractEmail(String token) {
		return parseClaims(token).get("email", String.class);
	}

	public String extractRoles(String token) {
		return parseClaims(token).get("roles", String.class);
	}

	public String extractUserId(String token) {
		return parseClaims(token).getSubject();
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}
}
