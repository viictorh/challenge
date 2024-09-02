package com.hyperativa.challenge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.hyperativa.challenge.entity.Batch;
import com.hyperativa.challenge.entity.Creditcard;

public class LineProcessorTest {

	private LineProcessor lineProcessor;
	private Function<Creditcard, Boolean> repositoryCheckMock;
	private BatchProcessHolder batchHolderMock;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp() {
		repositoryCheckMock = Mockito.mock(Function.class);
		batchHolderMock = Mockito.mock(BatchProcessHolder.class);
		lineProcessor = new LineProcessor(repositoryCheckMock);
	}

	@Test
	public void testProcessHeaderLine() {
		ArgumentCaptor<Batch> batchCaptor = ArgumentCaptor.forClass(Batch.class);

		String headerLine = "DESAFIO-HYPERATIVA           20240831LOTE0001000010";
		lineProcessor.processLine(headerLine, batchHolderMock);
		verify(batchHolderMock, times(1)).setCurrentBatch(batchCaptor.capture());

		Batch createdBatch = batchCaptor.getValue();
		assertEquals("DESAFIO-HYPERATIVA", createdBatch.getName());
		assertEquals(LocalDate.of(2024, 8, 31), createdBatch.getDate());
		assertEquals("LOTE0001", createdBatch.getDescription());
		assertEquals(10, createdBatch.getCount());
	}

	@Test
	public void testProcessFooterLine() {
		String footerLine = "LOTE0001000010";
		lineProcessor.processLine(footerLine, batchHolderMock);

		verify(batchHolderMock, times(1)).readyToSave();
		verify(batchHolderMock, times(1)).addTotalBatches();
	}

	@Test
	public void testProcessCreditCardLine() {
		String creditCardLine = "C10    4456897919999999  ";

		when(batchHolderMock.getCurrentBatch()).thenReturn(new Batch());
		when(repositoryCheckMock.apply(any(Creditcard.class))).thenReturn(false);

		lineProcessor.processLine(creditCardLine, batchHolderMock);

		verify(batchHolderMock, times(1)).addTotalCards();
	}

    @Test
    public void testProcessInvalidCreditCardLineWithoutBatch() {
        String creditCardLine = "C10    4456897919999999  ";
        when(batchHolderMock.getCurrentBatch()).thenReturn(null);

        lineProcessor.processLine(creditCardLine, batchHolderMock);

        verify(batchHolderMock, times(1)).addCardNumberToInvalidBatch(anyString());      
    }

    @ParameterizedTest
    @MethodSource("repeatedCardsScenarios")
    public void testProcessCreditCardLineWithDifferentBatches(String creditCardLine, Batch batch, boolean repoCheckReturns, boolean expectRepeated) {
       
        when(batchHolderMock.getCurrentBatch()).thenReturn(batch);
        when(repositoryCheckMock.apply(any(Creditcard.class))).thenReturn(repoCheckReturns);

       
        ArgumentCaptor<String> capturedCardNumber = ArgumentCaptor.forClass(String.class);

       
        lineProcessor.processLine(creditCardLine, batchHolderMock);
        
        if (expectRepeated) {
            verify(batchHolderMock, times(1)).addCardNumberToRepeated(capturedCardNumber.capture());
            String protectedCardNumber = lineProcessor.protectedCardNumber("4456897919999999");
            assertEquals(protectedCardNumber, capturedCardNumber.getValue());
        } else {
            verify(batchHolderMock, never()).addCardNumberToRepeated(anyString());
        }
    }


    @Test
    public void testProcessLineWithEmptyLine() {
        String emptyLine = "   ";
        lineProcessor.processLine(emptyLine, batchHolderMock);

        verify(batchHolderMock, never()).setCurrentBatch(any(Batch.class));
        verify(batchHolderMock, never()).readyToSave();
        verify(batchHolderMock, never()).addTotalBatches();
        verify(batchHolderMock, never()).addTotalCards();
        verify(batchHolderMock, never()).addCardNumberToInvalidBatch(anyString());
    }
    
    private static Stream<Arguments> repeatedCardsScenarios() {
        // Scenario 1: Batch contains same card on repository;
        Batch cardExistsOnRepo = new Batch();
        cardExistsOnRepo.setName("cardExistsOnRepo");
      
        // Scenario 2: Batch with no cards
        Batch batchWithNoCards = new Batch();
        batchWithNoCards.setName("batchWithNoCards");        
        batchWithNoCards.setCardsNumber(new HashSet<Creditcard>());
        
        // Scenario 3: Batch with a same card
        Batch batchWithSameCard = new Batch();
        batchWithSameCard.setName("batchWithSameCard");    
        Creditcard sameCard = new Creditcard();
        sameCard.setCardNumber("4456897919999999");
        sameCard.setBatch(batchWithSameCard);
        
        // Scenario 4: Batch with a different card
        Batch batchWithDifferentCard = new Batch();
        batchWithDifferentCard.setName("batchWithDifferentCard");    
        Creditcard differentCard = new Creditcard();
        differentCard.setCardNumber("123456");
        differentCard.setBatch(batchWithDifferentCard);
        
        return Stream.of(
            Arguments.of("C10    4456897919999999  ", cardExistsOnRepo, true, true),
            Arguments.of("C10    4456897919999999  ", batchWithNoCards, false, false),
            Arguments.of("C10    4456897919999999  ", batchWithNoCards, false, true),
            Arguments.of("C10    4456897919999999  ", batchWithDifferentCard, false, false)
        );
    }
}
