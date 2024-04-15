package com.matos.picpay.payments.authorization;

import com.matos.picpay.payments.exception.UnauthorizedTransactionException;
import com.matos.picpay.payments.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
public class AuthorizerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizerService.class);
    private final RestClient restClient;

    public AuthorizerService(RestClient.Builder restClient){
        this.restClient = restClient.baseUrl("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc").build();
    }
    public void authorize(Transaction transaction){
         var response = restClient.get().retrieve().toEntity(Authorization.class);
         if(response.getStatusCode().isError() || !Objects.requireNonNull(response.getBody()).isAuthorized())
             throw new UnauthorizedTransactionException("Transaction not authorized!");

         LOGGER.info("Transaction authorized: {}", transaction);
    }
}
