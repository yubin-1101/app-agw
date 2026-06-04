package com.company.agw.external.rcs;

import com.company.agw.common.exception.ExternalSystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class RcsClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.rcs.base-url}")
    private String baseUrl;

    public void recoverRcs(String messageId) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/rcs/recovery/{messageId}", messageId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new ExternalSystemException();
        }
    }
}
