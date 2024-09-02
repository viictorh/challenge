package com.hyperativa.challenge.exception;

import java.util.List;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerConfig {
	private final MessageSource messageSource;

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<HttpResponseException> handleAuthenticationException(AuthenticationException e,
			WebRequest request) {
		return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), null, request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponseException> handleAccessDeniedException(AccessDeniedException e,
			WebRequest request) {
		return buildResponse(HttpStatus.FORBIDDEN, e.getMessage(), null, request);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<HttpResponseException> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException e, WebRequest request) {
		List<FieldError> listFieldError = e.getBindingResult().getFieldErrors();

		return buildResponse(HttpStatus.BAD_REQUEST, listFieldError, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<HttpResponseException> handleGenericException(Exception e, WebRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null, request);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<HttpResponseException> handleRuntimeException(RuntimeException e, WebRequest request)
			throws RuntimeException {
		// Check for annotation
		ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
		HttpStatus responseCode = responseStatusAnnotation == null ? HttpStatus.INTERNAL_SERVER_ERROR
				: responseStatusAnnotation.code();
		return buildResponse(responseCode, e.getMessage(), null, request);
	}

	private ResponseEntity<HttpResponseException> buildResponse(HttpStatus status, String messageKey, Object[] args,
			WebRequest request) {

		args = Optional.ofNullable(args).orElse(new Object[] {});
		HttpResponseException responseException = new HttpResponseException(status, messageKey, args,
				request.getLocale(), messageSource);

		return new ResponseEntity<>(responseException, status);
	}

	private ResponseEntity<HttpResponseException> buildResponse(HttpStatus status, List<FieldError> listFieldError,
			WebRequest request) {

		HttpResponseException responseException = new HttpResponseException(status, listFieldError, request.getLocale(),
				messageSource);

		return new ResponseEntity<>(responseException, status);
	}
}