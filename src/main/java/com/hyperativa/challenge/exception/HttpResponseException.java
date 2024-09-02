package com.hyperativa.challenge.exception;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HttpResponseException {

	private int status;

	private String error;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> messages;

	private Long timestamp;

	public HttpResponseException(HttpStatus status, String error, String message, List<String> listMessage) {

		this.status = status.value();
		this.error = error;
		this.message = message;
		this.messages = listMessage;
		this.timestamp = System.currentTimeMillis();
	}

	public HttpResponseException(HttpStatus status, String message, Object[] args, Locale locale,
			MessageSource messageSource) {

		this(status, status.getReasonPhrase(), processMessage(message, args, locale, messageSource), null);
	}

	public HttpResponseException(HttpStatus status, List<FieldError> listError, Locale locale,
			MessageSource messageSource) {

		this(status, status.getReasonPhrase(), null, parseMessagesFromListFieldError(listError, locale, messageSource));
	}

	private static String processMessage(String message, Object[] args, Locale locale, MessageSource messageSource) {
		try {
			Object[] modifiedArgs = args != null
					? Arrays.stream(args).map(arg -> replaceArgumentIfKeyExists(arg, locale, messageSource)).toArray()
					: new Object[0];

			return messageSource.getMessage(message, modifiedArgs, locale);
		} catch (Exception e) {
			if (message == null)
				throw new IllegalArgumentException(
						"Durante o tratamento das mensagens de erros, a mensagem não pode ser nula.");

			return message;
		}

	}

	public static List<String> parseMessagesFromListFieldError(List<FieldError> listError, Locale locale,
			MessageSource messageSource) {

		try {
			return listError.stream().map(error -> processFieldError(error, locale, messageSource))
					.collect(Collectors.toList());

		} catch (NullPointerException e) {
			throw new IllegalArgumentException(
					"Durante o tratamento das mensagens de erros, a lista dos erros não pode ser nula.");
		}

	}

	private static String processFieldError(FieldError error, Locale locale, MessageSource messageSource) {

		if (error == null || (error.getArguments() == null && error.getDefaultMessage() == null)) {
			throw new IllegalArgumentException(
					"Erro de campo não pode ser nulo e deve ter argumentos ou mensagem padrão.");
		}

		try {
			Object[] modifiedArgs = error.getArguments() != null ? Arrays.stream(error.getArguments())
					.map(arg -> arg != null ? replaceArgumentIfKeyExists(arg, locale, messageSource) : "").toArray()
					: new Object[0];

			return messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), modifiedArgs, locale);
		} catch (Exception e) {
			return error.getDefaultMessage();
		}

	}

	private static Object replaceArgumentIfKeyExists(Object arg, Locale locale, MessageSource messageSource) {
		try {
			return (arg instanceof String) ? messageSource.getMessage((String) arg, null, locale) : arg;
		} catch (Exception e) {
			return arg;
		}
	}

}
