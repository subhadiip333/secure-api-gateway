package com.app.secure_api_gateway.client;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



@Component
public class ClientApiAdapter {

    private final RestTemplate restTemplate = new RestTemplate();

    public String callClientApi(
            String url,
            String encryptedPayload,
            String iv,  // Now just a plain string from properties
            String correlationId
    ) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.set("X-ENCRYPTION-IV", iv);  // Simple ASCII string
            headers.set("X-CORRELATION-ID", correlationId);

            HttpEntity<String> entity = new HttpEntity<>(encryptedPayload, headers);

            System.out.println("Calling Client API:");
            System.out.println("URL: " + url);
            System.out.println("IV: " + iv);
            System.out.println("Encrypted Payload: " + encryptedPayload);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error calling client API: " + e.getMessage());
            throw e;
        }
    }
}