package com.hyperativa.challenge.logging;

import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.RequestFilter;
import org.zalando.logbook.Sink;
import org.zalando.logbook.core.BodyFilters;
import org.zalando.logbook.core.Conditions;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.RequestFilters;
import org.zalando.logbook.json.JsonHttpLogFormatter;

@Configuration
public class LogbookConfig {

	@Bean
	RequestFilter customHeaderFilters() {
		return RequestFilter.merge(RequestFilters.defaultValue(),
				RequestFilters.replaceBody(message -> Conditions.contentType("text/plain").test(message)
						? "<Informação de upload suprimida>"
						: null));
	}

	@Bean
	public BodyFilter customBodyFilters() {
		return BodyFilter.merge(BodyFilters.defaultValue(), jsonPath("$.password").replace("<secret>"))
				.tryMerge(LoggingFilters.protectedToken());
	}

	@Bean
	public Sink customSink() {
		return new DefaultSink(new PrincipalHttpLogFormatter(new JsonHttpLogFormatter()), new DefaultHttpLogWriter());
	}
}