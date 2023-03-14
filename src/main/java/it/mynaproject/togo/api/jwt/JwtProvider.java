/*******************************************************************************
 * Copyright (c) Myna-Project SRL <info@myna-project.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 * - Myna-Project SRL <info@myna-project.org> - initial API and implementation
 ******************************************************************************/
package it.mynaproject.togo.api.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import it.mynaproject.togo.api.util.PropertiesFileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;

import javax.crypto.SecretKey;

@Component
public class JwtProvider {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	Properties prop = PropertiesFileReader.loadPropertiesFile();

	private SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	private int jwtExpiration = Integer.parseInt(prop.getProperty("jwtExpirationTime"));

	public String generateJwtToken(Authentication authentication) {

		User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpiration))
				.signWith(jwtSecret, SignatureAlgorithm.HS512)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {

		return Jwts.parserBuilder()
				.setSigningKey(jwtSecret)
				.build()
				.parseClaimsJws(token)
				.getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {

		try {
			Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
			return true;
		} catch (SecurityException e) {
			log.error("Invalid JWT signature -> Message: {} ", e);
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token -> Message: {}", e);
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token -> Message: {}", e);
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token -> Message: {}", e);
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty -> Message: {}", e);
		}

		return false;
	}
}
