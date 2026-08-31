package com.teya.ledger.service;

import com.teya.ledger.model.Transaction;

import com.teya.ledger.model.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LedgerService {

    private BigDecimal currentBalance = BigDecimal.ZERO;
    private final List<Transaction> transactionHistory = new ArrayList<>();

    public synchronized Transaction recordMovement(TransactionType type, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (type == TransactionType.WITHDRAWAL) {
            if (currentBalance.compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient balance");
            }
            currentBalance = currentBalance.subtract(amount);
        } else if (type == TransactionType.DEPOSIT) {
            currentBalance = currentBalance.add(amount);
        } else {
            throw new IllegalArgumentException("Invalid movement type. Use DEPOSIT or WITHDRAWAL");
        }

        Transaction tx = new Transaction(UUID.randomUUID(), type, amount, Instant.now());
        transactionHistory.add(tx);
        return tx;
    }

    public synchronized BigDecimal getBalance() {
        return currentBalance;
    }

    public List<Transaction> getHistory() {
        return transactionHistory;
    }
}