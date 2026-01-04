package com.app.secure_api_gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Set;

public class SensitiveDataMasker {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Set<String> PARTIAL_MASK_FIELDS =
            Set.of("pan", "aadhaar", "accountNumber", "cardNumber");

    private static final Set<String> FULL_MASK_FIELDS =
            Set.of("otp", "cvv", "pin");

    private static final Set<String> HASH_FIELDS =
            Set.of("token", "accessToken", "secret");

    public static String maskJson(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            maskNode(root);
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            return "UNABLE_TO_MASK_PAYLOAD";
        }
    }

    private static void maskNode(JsonNode node) {
        if (node.isObject()) {
            Iterator<String> fields = node.fieldNames();

            while (fields.hasNext()) {
                String fieldName = fields.next();
                JsonNode child = node.get(fieldName);

                if (child.isValueNode()) {
                    String value = child.asText();

                    if (PARTIAL_MASK_FIELDS.contains(fieldName)) {
                        ((com.fasterxml.jackson.databind.node.ObjectNode) node)
                                .put(fieldName, partialMask(value));

                    } else if (FULL_MASK_FIELDS.contains(fieldName)) {
                        ((com.fasterxml.jackson.databind.node.ObjectNode) node)
                                .put(fieldName, "******");

                    } else if (HASH_FIELDS.contains(fieldName)) {
                        ((com.fasterxml.jackson.databind.node.ObjectNode) node)
                                .put(fieldName, hash(value));
                    }

                } else {
                    maskNode(child);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                maskNode(item);
            }
        }
    }

    private static String partialMask(String value) {
        if (value.length() <= 4) return "****";
        return value.substring(0, 2) + "******" + value.substring(value.length() - 2);
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
