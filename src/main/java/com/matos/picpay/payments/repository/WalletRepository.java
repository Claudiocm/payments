package com.matos.picpay.payments.repository;

import com.matos.picpay.payments.wallet.Wallet;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, Long> {
}
