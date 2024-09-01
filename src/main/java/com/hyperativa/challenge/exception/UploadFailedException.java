package com.hyperativa.challenge.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UploadFailedException(String message, IOException e) {
		super(message, e);
	}

}
