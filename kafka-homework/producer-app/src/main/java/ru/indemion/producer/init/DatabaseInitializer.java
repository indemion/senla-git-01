package ru.indemion.producer.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.indemion.producer.service.BankAccountService;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final BankAccountService bankAccountService;
    private final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    public DatabaseInitializer(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bankAccountService.initBankAccounts();
        logger.info("BankAccounts inited");
    }
}