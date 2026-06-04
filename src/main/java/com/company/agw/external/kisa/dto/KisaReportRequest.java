package com.company.agw.external.kisa.dto;

import com.company.agw.domain.report.SpamReportRequest;
import com.company.agw.domain.spam.SpamMessageEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KisaReportRequest {

    private String messageId;
    private String messageType;
    private String reportReason;

    public static KisaReportRequest from(SpamReportRequest request, SpamMessageEntity message) {
        return KisaReportRequest.builder()
                .messageId(request.getMessageId())
                .messageType(message.getMessageType().name())
                .reportReason(request.getReportReason())
                .build();
    }
}
