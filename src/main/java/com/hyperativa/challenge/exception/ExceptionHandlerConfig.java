package com.hyperativa.challenge.exception;

import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import lombok.RequiredArgsConstructor;

//@ControllerAdvice
//@RequiredArgsConstructor
//public class ExceptionHandlerConfig {
//
//	private final MessageSource messageSource;
//
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<HttpResponseException> handleGenericException(Exception e, WebRequest request) {
//		e.printStackTrace();
//		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "error.request.internalServerError", null, request);
//	}
//
//	private ResponseEntity<HttpResponseException> buildResponse(HttpStatus status, String messageKey, Object[] args,
//			WebRequest request) {
//
//		args = Optional.ofNullable(args).orElse(new Object[] {});
//
//		HttpResponseException responseException = new HttpResponseException(status, messageKey, args,
//				request.getLocale(), messageSource);
//
//		return new ResponseEntity<>(responseException, status);
//	}
//
//}