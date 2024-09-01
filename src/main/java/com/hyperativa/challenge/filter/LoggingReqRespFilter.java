//package com.hyperativa.challenge.filter;
//
//import java.io.IOException;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class LoggingReqRespFilter extends OncePerRequestFilter {
//
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(request);
//		ContentCachingResponseWrapper cachingResponseWrapper = new ContentCachingResponseWrapper(response);
//
//		filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper);
//
//		String requestBody = getValueAsString(cachingRequestWrapper.getContentAsByteArray(),
//				cachingRequestWrapper.getCharacterEncoding());
//		String responseBody = getValueAsString(cachingResponseWrapper.getContentAsByteArray(),
//				cachingResponseWrapper.getCharacterEncoding());
//
//		String queryString = cachingRequestWrapper.getQueryString() != null ? cachingRequestWrapper.getQueryString()
//				: "";
////		logger.info("REQUEST [{}] [{}] {}", cachingRequestWrapper.getMethod(),
////				cachingRequestWrapper.getRequestURI() + queryString, requestBody);
//		logger.info("RESPONSE [{}] {}", cachingResponseWrapper.getStatus(), responseBody);
//
//		cachingResponseWrapper.copyBodyToResponse();
//	}
//
//	private String getValueAsString(byte[] contentAsByteArray, String characterEncoding) {
//		String dataAsString = "";
//		try {
//			dataAsString = new String(contentAsByteArray, characterEncoding);
//		} catch (Exception e) {
//			logger.error("Exception occurred while converting byte into an array: {}", e.getMessage());
//			e.printStackTrace();
//		}
//		return dataAsString;
//	}
//
//}