package com.hyperativa.challenge.logging;

import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

import org.zalando.logbook.BodyFilter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingFilters {

	public static BodyFilter protectedToken() {
		return jsonPath("$.jwt").replace(token -> {
			int firstDotIndex = token.indexOf('.');

			if (firstDotIndex != -1) {
				return token.substring(0, firstDotIndex + 1) + "<secret>";
			}
			return token;
		});
	}
}
