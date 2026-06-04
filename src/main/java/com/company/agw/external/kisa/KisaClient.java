package com.company.agw.external.kisa;

import com.company.agw.common.exception.ExternalSystemException;
import com.company.agw.external.kisa.dto.KisaReportRequest;
import com.company.agw.external.kisa.dto.KisaReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KisaClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.kisa.base-url}")
    private String baseUrl;

    public KisaReportResponse reportSpam(KisaReportRequest request) {
        try {
            return webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/report/spam")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(KisaReportResponse.class)
                    .block();
        } catch (Exception e) {
            throw new ExternalSystemException();
        }
    }
}
