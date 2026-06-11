package com.company.agw.external.rcs;

import com.company.agw.common.exception.ExternalSystemException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RcsClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.rcs.base-url}")
    private String baseUrl;

    @Value("${external.rcs.username:}")
    private String username;

    @Value("${external.rcs.password:}")
    private String password;

    public void recoverRcs(String messageId) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/rcs/recovery/{messageId}", messageId)
                    .headers(headers -> {
                        if (hasText(username) && hasText(password)) {
                            headers.setBasicAuth(username, password);
                        }
                    })
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.warn(
                    "RCS recovery API failed. uri={}, status={}, body={}",
                    baseUrl + "/rcs/recovery/" + messageId,
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );
            throw new ExternalSystemException();
        } catch (Exception e) {
            log.warn("RCS recovery API failed. uri={}", baseUrl + "/rcs/recovery/" + messageId, e);
            throw new ExternalSystemException();
        }
    }

    public void restoreMessage(
            String messageId,
            String originationMdn,
            String destinationMdn,
            String originationType
    ) {
        try {
            Map<String, String> body = new LinkedHashMap<>();
            body.put("message_id", messageId);
            body.put("origination_mdn", originationMdn);
            body.put("destination_mdn", destinationMdn);
            body.put("origination_type", originationType);

            webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/api/v1/message/restore")
                    .headers(headers -> {
                        if (hasText(username) && hasText(password)) {
                            headers.setBasicAuth(username, password);
                        }
                    })
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.warn(
                    "RCS restore API failed. uri={}, status={}, body={}, messageId={}, originationMdn={}, destinationMdn={}, originationType={}",
                    baseUrl + "/api/v1/message/restore",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    messageId,
                    originationMdn,
                    destinationMdn,
                    originationType
            );
            throw new ExternalSystemException();
        } catch (Exception e) {
            log.warn(
                    "RCS restore API failed. uri={}, messageId={}, originationMdn={}, destinationMdn={}, originationType={}",
                    baseUrl + "/api/v1/message/restore",
                    messageId,
                    originationMdn,
                    destinationMdn,
                    originationType,
                    e
            );
            throw new ExternalSystemException();
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
