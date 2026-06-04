package com.company.agw.domain.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpamReportRequest {

    private String token;
    private String userId;
    private String messageId;
    private String reportReason;
}
