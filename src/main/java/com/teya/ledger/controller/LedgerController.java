package com.teya.ledger.controller;

import com.teya.ledger.dto.MovementRequest;
import com.teya.ledger.service.LedgerService;
import com.teya.ledger.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/movements")
    public ResponseEntity<Transaction> recordMovement(@RequestBody MovementRequest request) {
        Transaction tx = ledgerService.recordMovement(request.type(), request.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(tx);
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance() {
        return ResponseEntity.ok(Map.of("balance", ledgerService.getBalance()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getHistory() {
        return ResponseEntity.ok(ledgerService.getHistory());
    }

}
