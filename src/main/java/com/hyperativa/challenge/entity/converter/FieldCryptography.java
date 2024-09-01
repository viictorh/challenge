package com.hyperativa.challenge.entity.converter;

import com.hyperativa.challenge.util.CryptographyUtil;

import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FieldCryptography implements AttributeConverter<String, String> {

	private final CryptographyUtil cryptographyUtil;

	@Override
	public String convertToDatabaseColumn(String s) {
		return cryptographyUtil.encrypt(s);
	}

	@Override
	public String convertToEntityAttribute(String s) {
		return cryptographyUtil.decrypt(s);
	}
}