package com.hyperativa.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AlreadyFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlreadyFoundException() {
		this("Cadastro já existe no banco de dados");
	}

	public AlreadyFoundException(String message) {
		super(message);
	}

}
