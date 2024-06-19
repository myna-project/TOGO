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
package it.mynaproject.togo.api.config;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Pbkdf2CustomEncoder implements PasswordEncoder {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public String encode(CharSequence rawPassword) {

		int iterations = 100000;
		int keyLength = 512;
		char[] chars = rawPassword.toString().toCharArray();
		byte[] salt = null;
		SecretKeyFactory skf = null;
		SecretKey skey = null;

		try {
			salt = getSalt();
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}

		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, keyLength);

		try {
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}

		try {
			skey = skf.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
			log.error(e.getMessage());
		}

		byte[] hash = skey.getEncoded();

		return "PBKDF2$sha512$" + iterations + "$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {

		String[] parts = encodedPassword.split("\\$");

		int iterations = Integer.parseInt(parts[2]);
		int keyLength = 512;
		char[] chars = rawPassword.toString().toCharArray();
		byte[] salt = Base64.getDecoder().decode(parts[3]);
		SecretKeyFactory skf = null;
		SecretKey skey = null;

		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, keyLength);

		try {
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}

		try {
			skey = skf.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
			log.error(e.getMessage());
		}

		byte[] hash2 = skey.getEncoded();

		String hash2t = "PBKDF2$sha512$" + iterations + "$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash2);

		boolean b = hash2t.equals(encodedPassword);

		return b;
	}

	private static byte[] getSalt() throws NoSuchAlgorithmException {

		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

		byte[] salt = new byte[16];

		sr.nextBytes(salt);

		return salt;
	}
}
