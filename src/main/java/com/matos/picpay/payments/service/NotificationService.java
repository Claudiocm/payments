package com.matos.picpay.payments.service;

import com.matos.picpay.payments.notification.NotificationProducer;
import com.matos.picpay.payments.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
   private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
   private final NotificationProducer notificationProducer;

    public NotificationService(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    public void notify(Transaction transaction){
         LOGGER.info("Nogifying transaction {}...", transaction);
         notificationProducer.sendNotification(transaction);
    }
}
