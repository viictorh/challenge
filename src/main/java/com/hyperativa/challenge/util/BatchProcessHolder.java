package com.hyperativa.challenge.util;

import java.util.HashSet;
import java.util.Set;

import com.hyperativa.challenge.dto.UploadResponse;
import com.hyperativa.challenge.entity.Batch;

public class BatchProcessHolder {
	private Batch currentBatch;
	private boolean readyToSave;
	private Set<String> invalidBatch = new HashSet<String>();
	private Set<String> repeated = new HashSet<String>();
	private int totalBatches = 0;
	private int totalCards = 0;

	public Batch getCurrentBatch() {
		return currentBatch;
	}

	public void setCurrentBatch(Batch currentBatch) {
		this.currentBatch = currentBatch;
	}

	public void readyToSave() {
		readyToSave = true;
	}

	public boolean isReadyToSave() {
		return readyToSave;
	}

	public void clearBatch() {
		currentBatch = null;
		readyToSave = false;
	}

	public void addCardNumberToInvalidBatch(String number) {
		invalidBatch.add(number);
	}

	public void addCardNumberToRepeated(String number) {
		repeated.add(number);
	}

	public void addTotalBatches() {
		totalBatches++;
	}

	public void addTotalCards() {
		totalCards++;
	}

	public UploadResponse getStats() {
		return new UploadResponse(invalidBatch.isEmpty() ? null : invalidBatch, repeated.isEmpty() ? null : repeated,
				totalBatches, totalCards);
	}
}