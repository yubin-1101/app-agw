package com.company.agw.domain.report;

import com.company.agw.common.response.PassResponseCode;
import com.company.agw.domain.spam.PassSpamMessageEntity;
import com.company.agw.domain.spam.PassSpamMessageMapper;
import com.company.agw.domain.user.PassUserIdentity;
import com.company.agw.domain.user.PassUserIdentityResolver;
import com.company.agw.external.kisa.KisaClient;
import com.company.agw.external.kisa.dto.KisaReportResponse;
import com.company.agw.external.kisa.dto.PassKisaReportRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassSpamReportService {

    private static final String KISA_SPAM_TYPE = "10";
    private static final String DEFAULT_STATUS = "SUCCESS";
    private static final String DEFAULT_FLAG = "0";

    private final PassUserIdentityResolver passUserIdentityResolver;
    private final PassSpamMessageMapper passSpamMessageMapper;
    private final SpamReportMapper spamReportMapper;
    private final KisaClient kisaClient;

    @Transactional
    public PassReportSpamMsgResponse reportSpamMsg(PassReportSpamMsgRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return PassReportSpamMsgResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isValidReportSpamMsgRequest(request)) {
            return PassReportSpamMsgResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (isUnsupportedSmsType(request.getSmsType())) {
            return PassReportSpamMsgResponse.fail(
                    userID,
                    PassResponseCode.UNSUPPORTED_FEATURE.getRetCode(),
                    PassResponseCode.UNSUPPORTED_FEATURE.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return PassReportSpamMsgResponse.notJoined();
            }

            ReportData reportData = decodeReportData(request.getReportData());
            PassSpamMessageEntity spamMessage = null;
            if (!"0".equals(request.getSmsSeq())) {
                spamMessage = passSpamMessageMapper.selectRecoverySpamSMS(
                        identity.custNum(),
                        request.getSmsType(),
                        request.getSmsSeq()
                );
                if (spamMessage == null) {
                    PassReportSpamMsgResponse response = PassReportSpamMsgResponse.fail(
                            userID,
                            PassResponseCode.REPORT_MESSAGE_NOT_FOUND.getRetCode(),
                            PassResponseCode.REPORT_MESSAGE_NOT_FOUND.getRetMsg()
                    );
                    insertReportHistory(identity.custNum(), "2", response.getRetMsg());
                    return response;
                }
            }

            KisaReportResponse kisaResponse = kisaClient.reportPassSpam(PassKisaReportRequest.builder()
                    .userID(userID)
                    .smsType(request.getSmsType())
                    .smsSeq(request.getSmsSeq())
                    .reportData(request.getReportData())
                    .build());

            PassReportSpamMsgResponse response = PassReportSpamMsgResponse.success(userID);
            spamReportMapper.insertKisaSktMessage(toKisaSktMessage(
                    identity.custNum(),
                    request,
                    spamMessage,
                    reportData,
                    kisaResponse
            ));
            insertReportHistory(identity.custNum(), "1", response.getRetMsg());
            return response;
        } catch (Exception e) {
            return PassReportSpamMsgResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    private PassSpamReportEntity toKisaSktMessage(
            String custNum,
            PassReportSpamMsgRequest request,
            PassSpamMessageEntity spamMessage,
            ReportData reportData,
            KisaReportResponse kisaResponse
    ) {
        PassSpamReportEntity entity = new PassSpamReportEntity();
        entity.setListSeq(resolveListSeq(kisaResponse, request.getSmsSeq()));
        entity.setSpamType(KISA_SPAM_TYPE);
        entity.setSrcNum(selectText(spamMessage == null ? null : spamMessage.getSrcNum(), reportData.spamNumber()));
        entity.setCustNum(selectText(spamMessage == null ? null : spamMessage.getCustNum(), reportData.destinationNumber(), custNum));
        entity.setCbNum(selectText(spamMessage == null ? null : spamMessage.getCbNum(), reportData.spamNumber()));
        entity.setRcvTime(selectText(spamMessage == null ? null : spamMessage.getRcvDt(), spamMessage == null ? null : spamMessage.getSmsClc(), reportData.receivedAt()));
        entity.setMsg(selectText(spamMessage == null ? null : spamMessage.getSmsMsg(), reportData.message(), reportData.raw()));
        entity.setSpamProbability(DEFAULT_FLAG);
        entity.setNumberOfSc(DEFAULT_FLAG);
        entity.setStatus(resolveStatus(kisaResponse));
        entity.setEmailFlag(DEFAULT_FLAG);
        return entity;
    }

    private ReportData decodeReportData(String reportData) {
        try {
            String decoded = new String(Base64.getDecoder().decode(reportData), StandardCharsets.UTF_8);
            List<String> fields = decoded.lines()
                    .map(String::trim)
                    .filter(this::hasText)
                    .toList();
            return new ReportData(
                    decoded,
                    valueAt(fields, 4),
                    valueAt(fields, 5),
                    valueAt(fields, 6),
                    fields.isEmpty() ? "" : fields.get(fields.size() - 1)
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid reportData", e);
        }
    }

    private String resolveListSeq(KisaReportResponse response, String smsSeq) {
        if (response == null) {
            return smsSeq;
        }
        return selectText(response.getMessageId(), response.getTransactionId(), smsSeq);
    }

    private String resolveStatus(KisaReportResponse response) {
        if (response == null) {
            return DEFAULT_STATUS;
        }
        return selectText(response.getResultCode(), DEFAULT_STATUS);
    }

    private void insertReportHistory(String custNum, String rst, String jobMsg) {
        if (hasText(custNum)) {
            try {
                spamReportMapper.insertPassReportSpamMsgHistory(custNum, rst, jobMsg);
            } catch (Exception ignored) {
                // History failure must not hide the report result returned to PASS.
            }
        }
    }

    private boolean isValidReportSpamMsgRequest(PassReportSpamMsgRequest request) {
        return request != null
                && hasText(request.getSmsType())
                && hasText(request.getSmsSeq())
                && hasText(request.getReportData())
                && byteLength(request.getSmsSeq()) <= 40
                && (isReportableSmsType(request.getSmsType()) || isUnsupportedSmsType(request.getSmsType()));
    }

    private boolean isReportableSmsType(String smsType) {
        return "1".equals(smsType) || "3".equals(smsType) || "4".equals(smsType);
    }

    private boolean isUnsupportedSmsType(String smsType) {
        return "5".equals(smsType) || "8".equals(smsType);
    }

    private String valueAt(List<String> values, int index) {
        return values.size() > index ? values.get(index) : "";
    }

    private String selectText(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private int byteLength(String value) {
        return value == null ? 0 : value.getBytes(StandardCharsets.UTF_8).length;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private record ReportData(
            String raw,
            String spamNumber,
            String receivedAt,
            String destinationNumber,
            String message
    ) {
    }
}
