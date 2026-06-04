package com.company.agw.domain.report;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpamReportResponse {

    private String messageId;
    private String reportStatus;
    private String externalTransactionId;
}
