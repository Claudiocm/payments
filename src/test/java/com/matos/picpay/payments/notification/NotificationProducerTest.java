package com.matos.picpay.payments.notification;

import com.matos.picpay.payments.transaction.Transaction;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        topics = "transaction-notification",
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NotificationProducerTest {

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private NotificationProducer notificationProducer;

    @Test
    @DisplayName("deve consumir uma mensagem na fila")
    void sendNotification() {
        //cenario
        Consumer<String, Transaction> consumer = createConsumer(Transaction.class);
        this.broker.consumeFromEmbeddedTopics(consumer, "transaction-notification");
        Transaction transaction = new Transaction(1L,1L, 2L, BigDecimal.valueOf(100), LocalDateTime.now());

        //acao
        notificationProducer.sendNotification(transaction);

        //validacao
        ConsumerRecords<String, Transaction> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5));
        assertThat(records)
                .hasSize(1)
                .allMatch(msg -> msg.value().equals(transaction));
    }

    private <V> Consumer<String, V> createConsumer(Class<V> classType) {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(TOPIC, "true", this.broker);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        DefaultKafkaConsumerFactory<String, V> consumerFactory = new DefaultKafkaConsumerFactory(
                consumerProps, new StringDeserializer(), new JsonDeserializer<>()
        );

        return consumerFactory.createConsumer();
    }
}