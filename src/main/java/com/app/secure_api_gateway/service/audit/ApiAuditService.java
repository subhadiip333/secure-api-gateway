package com.app.secure_api_gateway.service.audit;

import com.app.secure_api_gateway.entity.ApiAuditLog;
import com.app.secure_api_gateway.repo.ApiAuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ApiAuditService {

    private final ApiAuditLogRepository auditLogRepository;

    public ApiAuditService(ApiAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    //REQUEST ENTRY (ASYNC)
    @Async("auditExecutor")
    public void logRequest(ApiAuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
        } catch (Exception ex) {
            // log error only
        }
    }


    // RESPONSE EXIT (ASYNC)
    @Async("auditExecutor")
    public void logResponse(
            String correlationId,
            String responsePayload,
            Integer httpStatus,
            String status,
            String errorCode,
            String errorMessage
    ) {
        try {
            ApiAuditLog log = auditLogRepository
                    .findByCorrelationId(correlationId)
                    .orElse(null);

            if (log == null) return;

            log.setResponsePayload(responsePayload);
            log.setHttpStatus(httpStatus);
            log.setStatus(status);
            log.setErrorCode(errorCode);
            log.setErrorMessage(errorMessage);
            log.setResponseTime(LocalDateTime.now());

            long latency = Duration.between(
                    log.getRequestTime(),
                    log.getResponseTime()
            ).toMillis();

            log.setLatencyMs(latency);

            auditLogRepository.save(log);

        } catch (Exception ex) {
            // swallow exception
        }
    }
}
