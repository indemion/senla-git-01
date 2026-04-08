package ru.indemion.carservice.models.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.indemion.carservice.common.HealthStatus;
import ru.indemion.carservice.dto.HealthResponseDto;

@Service
@Transactional
public class HealthService {
    private static final Logger logger = LogManager.getLogger(HealthService.class);
    private final SessionFactory sessionFactory;

    public HealthService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public HealthResponseDto healthCheck() {
        HealthResponseDto responseDto = new HealthResponseDto();
        responseDto.setStatus(HealthStatus.UP);
        try {
            Query<Integer> query = sessionFactory.getCurrentSession().createNativeQuery("SELECT 1", Integer.class);
            query.getSingleResult();
            responseDto.setDbStatus(HealthStatus.UP);
        } catch (Exception e) {
            responseDto.setDbStatus(HealthStatus.DOWN);
            logger.error(e.getMessage());
        }

        return responseDto;
    }

    public HttpStatus getHttpStatus(HealthResponseDto responseDto) {
        if (responseDto.getStatus() == HealthStatus.UP && responseDto.getDbStatus() == HealthStatus.UP) {
            return HttpStatus.OK;
        }

        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
