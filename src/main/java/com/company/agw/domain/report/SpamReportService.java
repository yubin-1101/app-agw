package com.company.agw.domain.report;

import com.company.agw.auth.AuthService;
import com.company.agw.common.exception.BusinessException;
import com.company.agw.common.response.ResponseCode;
import com.company.agw.common.validation.RequestValidator;
import com.company.agw.domain.spam.SpamMessageEntity;
import com.company.agw.domain.spam.SpamMessageMapper;
import com.company.agw.external.kisa.KisaClient;
import com.company.agw.external.kisa.dto.KisaReportRequest;
import com.company.agw.external.kisa.dto.KisaReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpamReportService {

    private final AuthService authService;
    private final RequestValidator requestValidator;
    private final SpamMessageMapper spamMessageMapper;
    private final SpamReportMapper spamReportMapper;
    private final KisaClient kisaClient;

    @Transactional
    public SpamReportResponse report(SpamReportRequest request) {
        authService.authenticatePassRequest(request.getToken());
        requestValidator.requireText(request.getUserId(), "userId");
        requestValidator.requireText(request.getMessageId(), "messageId");

        SpamMessageEntity message = spamMessageMapper.selectSpamMessage(request.getUserId(), request.getMessageId());
        if (message == null) {
            throw new BusinessException(ResponseCode.MESSAGE_NOT_FOUND);
        }

        KisaReportResponse kisaResponse = kisaClient.reportSpam(KisaReportRequest.from(request, message));

        SpamReportEntity report = new SpamReportEntity();
        report.setUserId(request.getUserId());
        report.setMessageId(request.getMessageId());
        report.setReportReason(request.getReportReason());
        report.setReportStatus(kisaResponse.getResultCode());
        report.setExternalTransactionId(kisaResponse.getTransactionId());
        spamReportMapper.insertReportHistory(report);

        return SpamReportResponse.builder()
                .messageId(request.getMessageId())
                .reportStatus(kisaResponse.getResultCode())
                .externalTransactionId(kisaResponse.getTransactionId())
                .build();
    }
}
