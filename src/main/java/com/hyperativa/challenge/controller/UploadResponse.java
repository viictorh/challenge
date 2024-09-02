package com.hyperativa.challenge.controller;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

public record UploadResponse(@JsonInclude(JsonInclude.Include.NON_NULL) Set<String> invalidBatch,
		@JsonInclude(JsonInclude.Include.NON_NULL) Set<String> repeatedCards, int totalBatches, int totalSavedCards) {

}
