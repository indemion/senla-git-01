package ru.indemion.carservice.models.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.indemion.carservice.common.HealthStatus;
import ru.indemion.carservice.dto.HealthResponseDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthServiceTest {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private NativeQuery<Integer> query;

    @InjectMocks
    private HealthService healthService;

    @Test
    void healthCheck_whenDbQuerySuccessful_shouldSetDbStatusUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createNativeQuery("SELECT 1", Integer.class)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1);

        HealthResponseDto response = healthService.healthCheck();

        assertEquals(HealthStatus.UP, response.getStatus());
        assertEquals(HealthStatus.UP, response.getDbStatus());
        verify(sessionFactory).getCurrentSession();
        verify(session).createNativeQuery("SELECT 1", Integer.class);
        verify(query).getSingleResult();
    }

    @Test
    void healthCheck_whenDbQueryThrowsException_shouldSetDbStatusDown() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createNativeQuery("SELECT 1", Integer.class)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("DB error"));

        HealthResponseDto response = healthService.healthCheck();

        assertEquals(HealthStatus.UP, response.getStatus());
        assertEquals(HealthStatus.DOWN, response.getDbStatus());
        verify(sessionFactory).getCurrentSession();
        verify(session).createNativeQuery("SELECT 1", Integer.class);
        verify(query).getSingleResult();
    }

    @Test
    void getHttpStatus_whenBothStatusesUp_shouldReturnOk() {
        HealthResponseDto response = new HealthResponseDto();
        response.setStatus(HealthStatus.UP);
        response.setDbStatus(HealthStatus.UP);

        HttpStatus result = healthService.getHttpStatus(response);

        assertEquals(HttpStatus.OK, result);
    }

    @Test
    void getHttpStatus_whenAllDown_shouldReturnServiceUnavailable() {
        HealthResponseDto response = new HealthResponseDto();
        response.setStatus(HealthStatus.DOWN);
        response.setDbStatus(HealthStatus.DOWN);

        HttpStatus result = healthService.getHttpStatus(response);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result);
    }

    @Test
    void getHttpStatus_whenStatusDown_shouldReturnServiceUnavailable() {
        HealthResponseDto response = new HealthResponseDto();
        response.setStatus(HealthStatus.DOWN);
        response.setDbStatus(HealthStatus.UP);

        HttpStatus result = healthService.getHttpStatus(response);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result);
    }

    @Test
    void getHttpStatus_whenDbStatusDown_shouldReturnServiceUnavailable() {
        HealthResponseDto response = new HealthResponseDto();
        response.setStatus(HealthStatus.UP);
        response.setDbStatus(HealthStatus.DOWN);

        HttpStatus result = healthService.getHttpStatus(response);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result);
    }
}