package com.company.agw.external.mmsc;

import com.company.agw.common.exception.ExternalSystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class MmscClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.mmsc.base-url}")
    private String baseUrl;

    public void recoverMms(String messageId) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/mms/recovery/{messageId}", messageId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new ExternalSystemException();
        }
    }
}
