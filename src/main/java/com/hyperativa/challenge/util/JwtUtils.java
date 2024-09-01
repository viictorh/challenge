package com.hyperativa.challenge.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.hyperativa.challenge.security.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	@Value("${hyperativa.api.jwtSecret}")
	private String jwtSecret;

	@Value("${hyperativa.api.jwtExpirationMs}")
	private int jwtExpirationMs;

	private static Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	public String generateJwt(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return Jwts.builder().issuedAt(new Date()).expiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(getKey()).claim("id", userPrincipal.getId()).claim("login", userPrincipal.getUsername())
				.claim("profile", userPrincipal.getAuthorities()).compact();
	}

	public String getLoginFromJwtToken(String token) {
		return getJwtParser().parseSignedClaims(token).getPayload().get("login", String.class);
	}

	public boolean validateJwtToken(String authToken) {
		try {
			getJwtParser();
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: " + e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: " + e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: " + e.getMessage());
		}

		return false;
	}

	public boolean isExpired(String jwt) {
		if (jwt == null) {
			return true;
		}

		try {
			Date expiration = getJwtParser().parseSignedClaims(jwt).getPayload().getExpiration();
			return expiration.before(new Date());
		} catch (Exception e) {
			logger.error("JWT inválido: " + e.getMessage());
			return true;
		}
	}

	private JwtParser getJwtParser() {
		return Jwts.parser().verifyWith(getKey()).build();
	}

	private SecretKey getKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

}