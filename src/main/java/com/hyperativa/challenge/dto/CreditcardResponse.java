package com.hyperativa.challenge.dto;

public record CreditcardResponse(Integer id) {

	public static CreditcardResponse of(Integer id) {
		return new CreditcardResponse(id);
	}
}
