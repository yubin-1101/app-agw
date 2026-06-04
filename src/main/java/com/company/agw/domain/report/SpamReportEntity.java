package com.company.agw.domain.report;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpamReportEntity {

    private Long reportSeq;
    private String userId;
    private String messageId;
    private String reportReason;
    private String reportStatus;
    private String externalTransactionId;
    private LocalDateTime createdAt;
}
