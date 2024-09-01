package com.hyperativa.challenge.controller;

public record UploadResponse(String message) {

	public static UploadResponse success() {
		return new UploadResponse("Upload realizado com sucesso");
	}
}
