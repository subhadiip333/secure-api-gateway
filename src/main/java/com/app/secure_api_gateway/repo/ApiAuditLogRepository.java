package com.app.secure_api_gateway.repo;



import com.app.secure_api_gateway.entity.ApiAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiAuditLogRepository extends JpaRepository<ApiAuditLog, Long> {

    Optional<ApiAuditLog> findByCorrelationId(String correlationId);
}

