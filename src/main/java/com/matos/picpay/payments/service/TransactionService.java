package com.matos.picpay.payments.service;

import com.matos.picpay.payments.authorization.AuthorizerService;
import com.matos.picpay.payments.exception.InvalidTransactionException;
import com.matos.picpay.payments.repository.TransactionRepository;
import com.matos.picpay.payments.repository.WalletRepository;
import com.matos.picpay.payments.transaction.Transaction;
import com.matos.picpay.payments.wallet.Wallet;
import com.matos.picpay.payments.wallet.WalletType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private final NotificationService notificationService;


    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, AuthorizerService authorizerService,
                              NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    //transactional usado para fazer rollback das transações não validadas
    @Transactional
    public Transaction createTransaction(Transaction transaction){
        //1 - validar a transação com regras de negócios
        validated(transaction);
        //2 - criar transaction
        var newTransaction = transactionRepository.save(transaction);

        // 3 - debitar da carteira do pagador e creditar vendedor
        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payee()).get();

        walletRepository.save(walletPayer.debitValue(transaction.value()));
        walletRepository.save(walletPayee.creditValue(transaction.value()));

        // 4 - chamar serviços externos
        authorizerService.authorize(transaction);

        // 5 - notificação
        notificationService.notify(transaction);

        return newTransaction;
    }

    /**
     * - the payer has a common wallet
     * - the payer has enough balance
     * - the payer is not the payee
     */
    private void validated(Transaction transaction) {
        walletRepository.findById(transaction.payee())
                .map(payee -> walletRepository.findById(transaction.payer())
                .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
                        .orElseThrow(() -> new InvalidTransactionException("Invalid Transaction! - %s".formatted(transaction))))
                .orElseThrow(() -> new InvalidTransactionException("Invalid Transaction! - %s".formatted(transaction)));
    }

    private static boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue()
                && payer.balance().compareTo(transaction.value()) >= 0
                && !payer.id().equals(transaction.payee());
    }

    public List<Transaction> listTransaction() {
        return transactionRepository.findAll();
    }
}
