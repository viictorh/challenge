package com.hyperativa.challenge.dto;

import com.hyperativa.challenge.entity.Batch;
import com.hyperativa.challenge.entity.Creditcard;

import jakarta.validation.constraints.NotNull;

public record CreditcardDTO(String lineId, Integer number,
		@NotNull(message = "É necessário informar ao menos o número do cartão") String cardNumber, Batch batch

) {

	public Creditcard toEntity() {
		Creditcard creditcard = new Creditcard();
		creditcard.setBatch(batch);
		creditcard.setCardNumber(cardNumber);
		creditcard.setLineId(lineId);
		creditcard.setNumber(number);
		return creditcard;
	}
}
