package ru.indemion.consumer.service;

import common.dto.TransferMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.indemion.consumer.model.BankAccount;
import ru.indemion.consumer.model.Transfer;
import ru.indemion.consumer.model.TransferStatus;
import ru.indemion.consumer.repository.BankAccountRepository;
import ru.indemion.consumer.repository.TransferRepository;

@Service
public class TransferService {
    private final BankAccountRepository bankAccountRepository;
    private final TransferRepository transferRepository;

    public TransferService(BankAccountRepository bankAccountRepository, TransferRepository transferRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public void processTransfer(TransferMessage msg, BankAccount from, BankAccount to) {
        int newFromBalance = from.getBalance() - msg.getAmount();
        from.setBalance(newFromBalance);
        to.setBalance(to.getBalance() + msg.getAmount());
        bankAccountRepository.save(from);
        bankAccountRepository.save(to);

        transferRepository.create(getTransferFromMessage(msg, TransferStatus.SUCCESS));
    }

    @Transactional
    public void createErrorTransfer(TransferMessage msg) {
        transferRepository.create(getTransferFromMessage(msg, TransferStatus.ERROR));
    }

    private Transfer getTransferFromMessage(TransferMessage msg, TransferStatus status) {
        Transfer transfer = new Transfer();
        transfer.setId(msg.getId());
        transfer.setFromBankAccountId(msg.getFromId());
        transfer.setToBankAccountId(msg.getToId());
        transfer.setAmount(msg.getAmount());
        transfer.setStatus(status);
        return transfer;
    }
}