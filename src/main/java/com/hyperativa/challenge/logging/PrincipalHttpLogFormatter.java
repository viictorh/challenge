package com.hyperativa.challenge.logging;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.json.JsonHttpLogFormatter;

final class PrincipalHttpLogFormatter implements HttpLogFormatter {

	private final JsonHttpLogFormatter delegate;

	PrincipalHttpLogFormatter(final JsonHttpLogFormatter delegate) {
		this.delegate = delegate;
	}

	@Override
	public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
		final Map<String, Object> content = delegate.prepare(precorrelation, request);
		content.remove("remote");
		content.remove("origin");
		content.remove("protocol");
		content.remove("host");
		content.remove("scheme");
		content.remove("port");
		content.remove("path");
		content.put("Date", getFormattedDate());
		content.put("type", "REQUEST");
		return delegate.format(content);
	}

	@Override
	public String format(Correlation correlation, HttpResponse response) throws IOException {
		final Map<String, Object> content = delegate.prepare(correlation, response);
		content.remove("origin");
		content.put("type", "RESPONSE");
		return delegate.format(content);
	}

	private String getFormattedDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		String formattedDate = now.format(formatter);
		return formattedDate;
	}
}