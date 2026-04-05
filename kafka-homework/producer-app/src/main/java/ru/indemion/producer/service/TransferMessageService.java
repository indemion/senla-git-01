package ru.indemion.producer.service;

import common.dto.TransferMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.indemion.producer.model.BankAccount;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class TransferMessageService {
    private final Logger logger = LoggerFactory.getLogger(TransferMessageService.class);
    private final BankAccountService bankAccountService;
    private final KafkaTemplate<String, TransferMessage> kafkaTemplate;

    public TransferMessageService(BankAccountService bankAccountService,
                                  KafkaTemplate<String, TransferMessage> kafkaTemplate) {
        this.bankAccountService = bankAccountService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 200, initialDelay = 3000)
    public void generateAndSend() {
        send(generate());
    }

    private TransferMessage generate() {
        BankAccount from = bankAccountService.getRandomBankAccount();
        BankAccount to;
        do {
            to = bankAccountService.getRandomBankAccount();
        } while(from.getId().equals(to.getId()));

        Random random = new Random();

        return new TransferMessage(UUID.randomUUID(), from.getId(), to.getId(), random.nextInt(1, 1000));
    }

    private void send(TransferMessage msg) {
        CompletableFuture<SendResult<String, TransferMessage>> future = kafkaTemplate.send("transfers", msg);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Сообщение отправлено: {} в партицию {}", msg.getId(), result.getRecordMetadata().partition());
            } else {
                logger.error("не удалось отправить сообщение: {}", msg.getId(), ex);
            }
        });
    }
}
