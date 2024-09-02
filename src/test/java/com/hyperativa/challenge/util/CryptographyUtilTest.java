package com.hyperativa.challenge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class CryptographyUtilTest {
	private CryptographyUtil cryptographyUtil;

	private static final String KEY = "1234567890123456";
	private static final String INIT_VECTOR = "1234567890123456";

	@BeforeEach
	public void setUp() {
		cryptographyUtil = new CryptographyUtil();
		ReflectionTestUtils.setField(cryptographyUtil, "key", KEY);
		ReflectionTestUtils.setField(cryptographyUtil, "initVector", INIT_VECTOR);
		cryptographyUtil.init();
	}

	@Test
	public void testEncryptAndDecrypt() {
		String originalText = "This is a secret message";
		String encryptedText = cryptographyUtil.encrypt(originalText);

		assertNotNull(encryptedText, "Encrypted text should not be null");
		assertNotEquals(originalText, encryptedText);

		String decryptedText = cryptographyUtil.decrypt(encryptedText);

		assertNotNull(decryptedText, "Decrypted text should not be null");
		assertEquals(originalText, decryptedText, "Decrypted text should match the original text");
	}

	@Test
	public void testEncrypt_NullInput() {
		String encryptedText = cryptographyUtil.encrypt(null);
		assertNull(encryptedText, "Encrypting null should return null");
	}

	@Test
	public void testDecrypt_NullInput() {
		String decryptedText = cryptographyUtil.decrypt(null);
		assertNull(decryptedText, "Decrypting null should return null");
	}

	@Test
	public void testInitWithNullKeyOrVector_ShouldThrowException() {
		CryptographyUtil util = new CryptographyUtil();

		IllegalStateException exception = assertThrows(IllegalStateException.class, util::init,
				"Expected init() to throw, but it didn't");

		assertEquals("É preciso adicionar as chaves de segurança às variaveis de ambiente", exception.getMessage());
	}
}
