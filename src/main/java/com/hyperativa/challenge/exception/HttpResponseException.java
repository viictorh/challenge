//package com.hyperativa.challenge.exception;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//import org.springframework.context.MessageSource;
//import org.springframework.http.HttpStatus;
//import org.springframework.validation.FieldError;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
///**
// * Classe que representa uma exceção personalizada para padronização das
// * respostas HTTP.
// * <p>
// * * @Author Sávio Martins
// *
// * @Version 1.0
// * @Since 18 de dezembro de 2023
// * @LastModified 25 de abril de 2024
// * @LastModifiedBy Abner Amós
// */
//@Data
//@NoArgsConstructor
//public class HttpResponseException {
//
//	/**
//	 * O status HTTP da resposta.
//	 */
//	private int status;
//
//	/**
//	 * A descrição do erro.
//	 */
//	private String error;
//
//	/**
//	 * A mensagem de erro. Pode ser nula e se nula não é serializada;
//	 */
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	private String message;
//
//	/**
//	 * Uma lista de mensagens de erro adicionais. Pode ser nula e se nula não é
//	 * serializada.
//	 */
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	private List<String> messages;
//
//	/**
//	 * O carimbo de data e hora da exceção, representado como milissegundos desde o
//	 * ocorrido.
//	 */
//	private Long timestamp;
//
//	/**
//	 * Constrói uma instância de HttpResponseException com os parâmetros fornecidos.
//	 *
//	 * @param status      O status HTTP da resposta.
//	 * @param error       A descrição do erro.
//	 * @param message     A mensagem de erro.
//	 * @param listMessage Uma lista de mensagens de erro adicionais.
//	 */
//	public HttpResponseException(HttpStatus status, String error, String message, List<String> listMessage) {
//
//		this.status = status.value();
//		this.error = error;
//		this.message = message;
//		this.messages = listMessage;
//		this.timestamp = System.currentTimeMillis();
//	}
//
//	/**
//	 * Constrói uma instância de HttpResponseException com os parâmetros fornecidos.
//	 *
//	 * @param status        O status HTTP da resposta.
//	 * @param message       A mensagem de erro chave.
//	 * @param args          Os argumentos da mensagem de erro.
//	 * @param locale        O objeto {@link Locale} que determina a localização da
//	 *                      mensagem.
//	 * @param messageSource O {@link MessageSource} para obtenção das mensagens
//	 *                      localizadas.
//	 */
//	public HttpResponseException(HttpStatus status, String message, Object[] args, Locale locale,
//			MessageSource messageSource) {
//
//		this(status, status.getReasonPhrase(), processMessage(message, args, locale, messageSource), null);
//	}
//
//	/**
//	 * Constrói uma instância de HttpResponseException com os parâmetros fornecidos.
//	 *
//	 * @param status        O status HTTP da resposta.
//	 * @param listError     A lista de erros de campo a serem processados.
//	 * @param locale        O objeto {@link Locale} que determina a localização da
//	 *                      mensagem.
//	 * @param messageSource O {@link MessageSource} para obtenção das mensagens
//	 *                      localizadas.
//	 */
//	public HttpResponseException(HttpStatus status, List<FieldError> listError, Locale locale,
//			MessageSource messageSource) {
//
//		this(status, status.getReasonPhrase(), null, parseMessagesFromListFieldError(listError, locale, messageSource));
//	}
//
//	/**
//	 * Processa uma mensagem localizada com base em seus argumentos e locale.
//	 * <p>
//	 * Este método substitui argumentos se uma chave de mensagem existir, retornando
//	 * a mensagem localizada correspondente.
//	 *
//	 * @param message       A chave da mensagem a ser processada.
//	 * @param args          Os argumentos da mensagem.
//	 * @param locale        O objeto {@link Locale} que determina a localização da
//	 *                      mensagem.
//	 * @param messageSource O {@link MessageSource} para obtenção das mensagens
//	 *                      localizadas.
//	 *
//	 * @return A mensagem localizada após o processamento.
//	 */
//	private static String processMessage(String message, Object[] args, Locale locale, MessageSource messageSource) {
//		try {
//			Object[] modifiedArgs = args != null
//					? Arrays.stream(args).map(arg -> replaceArgumentIfKeyExists(arg, locale, messageSource)).toArray()
//					: new Object[0];
//
//			return messageSource.getMessage(message, modifiedArgs, locale);
//		} catch (Exception e) {
//			if (message == null)
//				throw new IllegalArgumentException(
//						"Durante o tratamento das mensagens de erros, a mensagem não pode ser nula.");
//
//			return message;
//		}
//
//	}
//
//	/**
//	 * Converte uma lista de erros de campo ({@link FieldError}) em uma lista de
//	 * mensagens de erro localizadas.
//	 *
//	 * @param listError     A lista de erros de campo a serem processados.
//	 * @param locale        O objeto {@link Locale} que determina a localização da
//	 *                      mensagem.
//	 * @param messageSource O {@link MessageSource} para obtenção das mensagens
//	 *                      localizadas.
//	 *
//	 * @return Uma lista de mensagens de erro localizadas.
//	 */
//	public static List<String> parseMessagesFromListFieldError(List<FieldError> listError, Locale locale,
//			MessageSource messageSource) {
//
//		try {
//			return listError.stream().map(error -> processFieldError(error, locale, messageSource))
//					.collect(Collectors.toList());
//
//		} catch (NullPointerException e) {
//			throw new IllegalArgumentException(
//					"Durante o tratamento das mensagens de erros, a lista dos erros não pode ser nula.");
//		}
//
//	}
//
//	/**
//	 * Processa um erro de campo ({@link FieldError}) e retorna uma mensagem de erro
//	 * localizada.
//	 *
//	 * @param error         O erro de campo a ser processado.
//	 * @param locale        O objeto {@link Locale} que determina a localização da
//	 *                      mensagem.
//	 * @param messageSource O {@link MessageSource} para obtenção das mensagens
//	 *                      localizadas.
//	 *
//	 * @return A mensagem de erro localizada.
//	 */
//	private static String processFieldError(FieldError error, Locale locale, MessageSource messageSource) {
//
//		if (error == null || (error.getArguments() == null && error.getDefaultMessage() == null)) {
//			throw new IllegalArgumentException(
//					"Erro de campo não pode ser nulo e deve ter argumentos ou mensagem padrão.");
//		}
//
//		try {
//			Object[] modifiedArgs = error.getArguments() != null ? Arrays.stream(error.getArguments())
//					.map(arg -> arg != null ? replaceArgumentIfKeyExists(arg, locale, messageSource) : "").toArray()
//					: new Object[0];
//
//			return messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), modifiedArgs, locale);
//		} catch (Exception e) {
//			return error.getDefaultMessage();
//		}
//
//	}
//
//	/**
//	 * Substitui um argumento se uma chave de mensagem existir, retornando a
//	 * mensagem localizada correspondente. Se não existir retorna simplesmente o
//	 * argumento.
//	 *
//	 * @param arg           O argumento a ser substituído ou a chave de mensagem.
//	 * @param locale        O objeto {@link Locale} que determina a localização da
//	 *                      mensagem.
//	 * @param messageSource O {@link MessageSource} para obtenção das mensagens
//	 *                      localizadas.
//	 * @return A mensagem localizada ou o argumento original, se nenhuma chave de
//	 *         mensagem for encontrada.
//	 */
//	private static Object replaceArgumentIfKeyExists(Object arg, Locale locale, MessageSource messageSource) {
//		try {
//			return (arg instanceof String) ? messageSource.getMessage((String) arg, null, locale) : arg;
//		} catch (Exception e) {
//			return arg;
//		}
//	}
//
//}
