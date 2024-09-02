package com.hyperativa.challenge.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.hyperativa.challenge.entity.Batch;
import com.hyperativa.challenge.entity.Creditcard;

public class LineProcessor {
	private static final int HEADER_SIZE = 51;
	private static final int FOOTER_SIZE = 14;
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
	private final Logger logger = LoggerFactory.getLogger(LineProcessor.class);
	private Function<Creditcard, Boolean> repositoryCheck;

	public LineProcessor(Function<Creditcard, Boolean> repositoryCheck) {
		this.repositoryCheck = repositoryCheck;
	}

	public void processLine(String line, BatchProcessHolder batchHolder) {
		if (!StringUtils.hasText(line)) {
			return;
		}

		int lineLength = line.trim().length();
		if (lineLength == HEADER_SIZE) {
			batchHolder.setCurrentBatch(createBatchFromHeader(line));
		} else if (lineLength == FOOTER_SIZE) {
			batchHolder.readyToSave();
			batchHolder.addTotalBatches();
		} else {
			processCreditCardLine(line, batchHolder);
		}
	}

	private Batch createBatchFromHeader(String line) {
		Batch batch = new Batch();
		batch.setName(line.substring(0, 29).trim());
		batch.setDate(LocalDate.parse(line.substring(29, 37), DATE_FORMATTER));
		batch.setDescription(line.substring(37, 45));
		batch.setCount(Integer.parseInt(line.substring(45).trim()));
		return batch;
	}

	private void processCreditCardLine(String line, BatchProcessHolder batchHolder) {
		Batch batch = batchHolder.getCurrentBatch();
		Creditcard creditCard = new Creditcard();
		creditCard.setCardNumber(line.substring(7).trim());
		String protectedCardNumber = protectedCardNumber(creditCard.getCardNumber());
		if (batch == null) {
			batchHolder.addCardNumberToInvalidBatch(protectedCardNumber);
			logger.warn("Tentativa de processamento de cartão sem batch relacionado: " + protectedCardNumber);
			return;
		}

		if (batch.getCardsNumber() != null && batch.getCardsNumber().contains(creditCard)
				|| repositoryCheck.apply(creditCard)) {
			batchHolder.addCardNumberToRepeated(protectedCardNumber);
			logger.warn(
					String.format("Lote: %s - cartão já cadastrado: %s", batch.getDescription(), protectedCardNumber));
			return;
		}

		creditCard.setLineId(line.substring(0, 1));
		creditCard.setNumber(Integer.parseInt(line.substring(1, 7).trim()));
		creditCard.setBatch(batch);
		batch.getCardsNumber().add(creditCard);
		batchHolder.addTotalCards();
	}

	private String protectedCardNumber(String cardNumber) {
		int lengthToMask = cardNumber.length() - 4;
		String maskedPart = "*".repeat(lengthToMask);
		String visiblePart = cardNumber.substring(lengthToMask);
		return maskedPart + visiblePart;
	}
}
