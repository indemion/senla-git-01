package ru.indemion.producer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.indemion.producer.model.BankAccount;
import ru.indemion.producer.repository.BankAccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class BankAccountService {
    private static final int BANK_ACCOUNT_AMOUNT = 1000;
    private final BankAccountRepository bankAccountRepository;
    private final Map<Integer, BankAccount> bankAccounts = new HashMap<>();

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public void initBankAccounts() {
        if (bankAccountRepository.count() == 0) {
            for (int i = 1; i <= BANK_ACCOUNT_AMOUNT; i++) {
                BankAccount bankAccount = new BankAccount(BANK_ACCOUNT_AMOUNT);
                bankAccounts.put(i, bankAccount);
                bankAccountRepository.save(bankAccount);
            }
        } else {
            bankAccountRepository.findAll()
                    .forEach(bankAccount -> bankAccounts.put(bankAccount.getId(), bankAccount));
        }
    }

    public BankAccount getRandomBankAccount() {
        Random random = new Random();
        return bankAccounts.get(random.nextInt(1, BANK_ACCOUNT_AMOUNT));
    }
}
