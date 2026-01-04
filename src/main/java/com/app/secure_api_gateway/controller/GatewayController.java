package com.app.secure_api_gateway.controller;

import com.app.secure_api_gateway.service.GatewayService;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }


    @PostMapping("/invoke")
    public ResponseEntity<String> invokeApi(
            @RequestHeader("X-CLIENT-ID") String clientId,
            @RequestHeader("X-API-NAME") String apiName,
            @RequestHeader("X-CLIENT-URL") String clientUrl,
            @RequestBody String requestPayload
    ) throws Exception {

        String response = gatewayService.processRequest(
                clientId,
                apiName,
                clientUrl,
                HttpMethod.POST,
                requestPayload
        );

        return ResponseEntity.ok(response);
    }
}
