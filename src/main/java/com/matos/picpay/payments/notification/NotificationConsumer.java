package com.matos.picpay.payments.notification;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.matos.picpay.payments.exception.NotificationException;
import com.matos.picpay.payments.transaction.Transaction;

@Service
public class NotificationConsumer {
    private final RestClient restClient;
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    public NotificationConsumer(RestClient.Builder restClient) {
        this.restClient = restClient.baseUrl("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc").build();
    }

    @KafkaListener(topics = "transaction-notification", groupId = "picpay-backend")
    public void receiverNotification(Transaction transaction) {
        log.info("notifying transaction: {}", transaction);
        var response = restClient.get()
                .retrieve()
                .toEntity(Notification.class);

        if (response.getStatusCode().isError() || !Objects.requireNonNull(response.getBody()).message())
            throw new NotificationException("Error sending Notification!");

        log.info("Transaction authorized: {}", transaction);
    }
}
