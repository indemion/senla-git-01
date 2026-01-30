package ru.indemion.carservice.ui.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OperationLogger {
    private final Logger logger = LogManager.getLogger();

    public OperationLogger() {
    }

    public void log(String operationName, Runnable action) {
        logger.info("Начало операции: {}", operationName);
        try {
            action.run();
            logger.info("Операция завершена успешно: {}", operationName);
        } catch (Exception e) {
            logger.error("Ошибка в операции '{}': {}", operationName, e.getMessage(), e);
            throw e;
        }
    }
}
