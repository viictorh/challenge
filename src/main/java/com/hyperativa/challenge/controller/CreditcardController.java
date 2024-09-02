package com.hyperativa.challenge.controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyperativa.challenge.dto.CreditcardDTO;
import com.hyperativa.challenge.dto.CreditcardResponse;
import com.hyperativa.challenge.dto.UploadResponse;
import com.hyperativa.challenge.entity.Creditcard;
import com.hyperativa.challenge.service.CreditcardService;

import jakarta.servlet.ServletException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/creditcard")
@RequiredArgsConstructor
public class CreditcardController {

	private final CreditcardService creditcardService;

	@GetMapping(value = "find")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
	public CreditcardResponse findCardnumber(@RequestParam(name = "creditcard", required = true) String creditcard) {
		Creditcard foundCreditCard = creditcardService.findCreditCard(creditcard);
		return CreditcardResponse.of(foundCreditCard.getId());
	}

	@PostMapping(value = "insert")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public CreditcardResponse save(@Valid @RequestBody CreditcardDTO creditcard) {
		Creditcard creditCard = creditcardService.save(creditcard);
		return CreditcardResponse.of(creditCard.getId());
	}

	@PostMapping(value = "file-upload", consumes = MediaType.TEXT_PLAIN_VALUE)
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public UploadResponse saveFile(InputStream inputStream) throws IOException, ServletException {
		return creditcardService.saveFromFile(inputStream);
	}

}
