package com.teya.ledger.service;

import com.teya.ledger.model.Transaction;
import com.teya.ledger.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LedgerServiceTest {

    private LedgerService ledgerService;

    @BeforeEach
    void setUp() {
        // Instantiate a fresh service instance before each test to guarantee a zero balance
        ledgerService = new LedgerService();
    }

    @Test
    void shouldStartWithZeroBalanceAndEmptyHistory() {
        assertEquals(BigDecimal.ZERO, ledgerService.getBalance());
        assertTrue(ledgerService.getHistory().isEmpty());
    }

    @Test
    void shouldIncreaseBalanceOnDeposit() {
        // Act
        Transaction tx = ledgerService.recordMovement(TransactionType.DEPOSIT, new BigDecimal("150.50"));

        // Assert
        assertEquals(new BigDecimal("150.50"), ledgerService.getBalance());
        assertNotNull(tx.id());
        assertEquals(TransactionType.DEPOSIT, tx.type());
        assertEquals(new BigDecimal("150.50"), tx.amount());
        assertNotNull(tx.timestamp());
    }

    @Test
    void shouldDecreaseBalanceOnWithdrawal() {
        // Arrange (Setup initial funds)
        ledgerService.recordMovement(TransactionType.DEPOSIT, new BigDecimal("200.00"));

        // Act
        Transaction tx = ledgerService.recordMovement(TransactionType.WITHDRAWAL, new BigDecimal("75.25"));

        // Assert
        assertEquals(new BigDecimal("124.75"), ledgerService.getBalance());
        assertEquals(TransactionType.WITHDRAWAL, tx.type());
        assertEquals(new BigDecimal("75.25"), tx.amount());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingMoreThanBalance() {
        // Arrange (Deposit some money first)
        ledgerService.recordMovement(TransactionType.DEPOSIT, new BigDecimal("50.00"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ledgerService.recordMovement(TransactionType.WITHDRAWAL, new BigDecimal("50.01"));
        });

        assertEquals("Insufficient balance", exception.getMessage());
        // Verify balance was untouched
        assertEquals(new BigDecimal("50.00"), ledgerService.getBalance());
    }

    @Test
    void shouldThrowExceptionForZeroOrNegativeAmounts() {
        // Assert Negative Deposits
        IllegalArgumentException negativeEx = assertThrows(IllegalArgumentException.class, () -> {
            ledgerService.recordMovement(TransactionType.DEPOSIT, new BigDecimal("-10.00"));
        });
        assertEquals("Amount must be greater than zero", negativeEx.getMessage());

        // Assert Zero Withdrawals
        IllegalArgumentException zeroEx = assertThrows(IllegalArgumentException.class, () -> {
            ledgerService.recordMovement(TransactionType.WITHDRAWAL, BigDecimal.ZERO);
        });
        assertEquals("Amount must be greater than zero", zeroEx.getMessage());
    }

    @Test
    void shouldMaintainSequentialTransactionHistory() {
        // Act
        ledgerService.recordMovement(TransactionType.DEPOSIT, new BigDecimal("100.00"));
        ledgerService.recordMovement(TransactionType.WITHDRAWAL, new BigDecimal("30.00"));
        ledgerService.recordMovement(TransactionType.DEPOSIT, new BigDecimal("10.00"));

        // Assert
        List<Transaction> history = ledgerService.getHistory();
        assertEquals(3, history.size());

        // Check ordering and correctness
        assertEquals(TransactionType.DEPOSIT, history.get(0).type());
        assertEquals(new BigDecimal("100.00"), history.get(0).amount());

        assertEquals(TransactionType.WITHDRAWAL, history.get(1).type());
        assertEquals(new BigDecimal("30.00"), history.get(1).amount());

        assertEquals(TransactionType.DEPOSIT, history.get(2).type());
        assertEquals(new BigDecimal("10.00"), history.get(2).amount());
    }
}