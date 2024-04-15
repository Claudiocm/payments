package com.matos.picpay.payments.repository;

import com.matos.picpay.payments.transaction.Transaction;
import org.springframework.data.repository.ListCrudRepository;

public interface TransactionRepository extends ListCrudRepository<Transaction, Long> {
}
