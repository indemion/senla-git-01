package ru.indemion.consumer.service;

import common.dto.TransferMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.indemion.consumer.model.BankAccount;
import ru.indemion.consumer.repository.BankAccountRepository;

import java.util.List;

@Service
public class TransferMessageService {
    private static final Logger logger = LoggerFactory.getLogger(TransferMessageService.class);
    private final BankAccountRepository bankAccountRepository;
    private final TransferService transferService;

    public TransferMessageService(BankAccountRepository bankAccountRepository, TransferService transferService) {
        this.bankAccountRepository = bankAccountRepository;
        this.transferService = transferService;
    }

    @KafkaListener(
            topics = "transfers",
            groupId = "transfer-group",
            batch = "true",
            containerFactory = "kafkaBatchListenerContainerFactory")
    public void listen(List<TransferMessage> transferMessageList, Acknowledgment ack) {
        logger.info("Начата пакетная обработка {} сообщений", transferMessageList.size());
        try {
            transferMessageList.forEach(this::processTransferMessage);
            ack.acknowledge();
        } catch (Exception e) {
            logger.error("Не удалось обработать пакет сообщений", e);
        }
    }

    private void processTransferMessage(TransferMessage msg) {
        logger.info("Начата обработка сообщения: {}", msg.getId());

        BankAccount from = bankAccountRepository.findById(msg.getFromId());
        BankAccount to = bankAccountRepository.findById(msg.getToId());

        if (from == null || to == null) {
            logger.error("BankAccount не найден: from={}, to={}", msg.getFromId(), msg.getToId());
            return;
        }

        if (from.getBalance().compareTo(msg.getAmount()) < 0) {
            logger.error("Недостаточно средств: bank_account={}, balance={}, amount={}",
                    from.getId(), from.getBalance(), msg.getAmount());
            return;
        }

        try {
            transferService.processTransfer(msg, from, to);
            logger.info("Transfer прошёл успешно: {}", msg.getId());
        } catch (Exception e) {
            logger.error("Transaction failed for transfer: {}", msg.getId(), e);
            transferService.createErrorTransfer(msg);
        }
    }

//    @KafkaListener(
//            topics = "transfers",
//            groupId = "transfer-group",
//            containerFactory = "kafkaListenerContainerFactory")
//    public void listen(TransferMessage msg) {
//        logger.info("Начата обработка сообщения: {}", msg.getId());
//
//        BankAccount from = bankAccountRepository.findById(msg.getFromId());
//        BankAccount to = bankAccountRepository.findById(msg.getToId());
//
//        if (from == null || to == null) {
//            logger.error("BankAccount не найден: from={}, to={}", msg.getFromId(), msg.getToId());
//            return;
//        }
//
//        if (from.getBalance().compareTo(msg.getAmount()) < 0) {
//            logger.error("Недостаточно средств: bank_account={}, balance={}, amount={}",
//                    from.getId(), from.getBalance(), msg.getAmount());
//            return;
//        }
//
//        try {
//            transferService.processTransfer(msg, from, to);
//            logger.info("Transfer прошёл успешно: {}", msg.getId());
//        } catch (Exception e) {
//            logger.error("Transaction failed for transfer: {}", msg.getId(), e);
//            transferService.createErrorTransfer(msg);
//        }
//    }
}
