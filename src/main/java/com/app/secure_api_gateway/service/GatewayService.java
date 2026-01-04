package com.app.secure_api_gateway.service;

import com.app.secure_api_gateway.client.ClientApiAdapter;
import com.app.secure_api_gateway.entity.ApiAuditLog;
import com.app.secure_api_gateway.repo.ApiAuditLogRepository;
import com.app.secure_api_gateway.service.crypto.AesEncryptionService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.app.secure_api_gateway.util.SensitiveDataMasker.maskJson;

@Service
public class GatewayService {

    private final AesEncryptionService aesService;
    private final ClientApiAdapter clientApiAdapter;
    private final ApiAuditLogRepository auditRepository;

    @Value("${crypto.aes.key}")
    private String aesIv;

    public GatewayService(
            AesEncryptionService aesService,
            ClientApiAdapter clientApiAdapter,
            ApiAuditLogRepository auditRepository) {

        this.aesService = aesService;
        this.clientApiAdapter = clientApiAdapter;
        this.auditRepository = auditRepository;
    }

    public String processRequest(
            String clientId,
            String apiName,
            String clientUrl,
            HttpMethod httpMethod,
            String plainRequest
    ) throws Exception {

        String correlationId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();

        ApiAuditLog audit = new ApiAuditLog();
        audit.setCorrelationId(correlationId);
        audit.setClientId(clientId);
        audit.setApiName(apiName);
        audit.setHttpMethod(httpMethod.name());
        audit.setEndpoint(clientUrl);
        audit.setIsPayloadEncrypted("Y");

        try {
            //Encrypt request
            String encryptedPayload = aesService.encrypt(plainRequest);

            audit.setRequestPayload(maskJson(plainRequest));

            System.out.println("encryptedPayload----> "+encryptedPayload);

            //Call client API
            String encryptedResponse = clientApiAdapter.callClientApi(
                    clientUrl,
                    encryptedPayload,
                    aesIv,  // Pass the IV from properties
                    correlationId
            );

            System.out.println("encryptedResponse---> "+encryptedResponse);

            //Decrypt response
            String decryptedResponse = aesService.decrypt(encryptedResponse);

            System.out.println("decryptedResponse---> "+decryptedResponse);

            audit.setResponsePayload(maskJson(decryptedResponse));
            audit.setHttpStatus(200);
            audit.setStatus("SUCCESS");

            return decryptedResponse;

        } catch (Exception ex) {
            audit.setStatus("FAILED");
            audit.setErrorCode("GATEWAY_ERROR");
            audit.setErrorMessage(ex.getMessage());
            throw ex;
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            audit.setResponseTime(endTime);
            audit.setLatencyMs(Duration.between(startTime, endTime).toMillis());
            auditRepository.save(audit);
        }
    }
}