package com.matos.picpay.payments.controller;

import com.matos.picpay.payments.service.TransactionService;
import com.matos.picpay.payments.transaction.Transaction;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction){
        return transactionService.createTransaction(transaction);
    }

    @GetMapping("/list")
    public List<Transaction> list(){
        return transactionService.listTransaction();
    }
}
