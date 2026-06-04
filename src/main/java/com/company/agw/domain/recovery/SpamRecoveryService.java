package com.company.agw.domain.recovery;

import com.company.agw.auth.AuthService;
import com.company.agw.common.exception.BusinessException;
import com.company.agw.common.response.ResponseCode;
import com.company.agw.common.validation.RequestValidator;
import com.company.agw.domain.spam.MessageType;
import com.company.agw.domain.spam.SpamMessageEntity;
import com.company.agw.domain.spam.SpamMessageMapper;
import com.company.agw.external.mmsc.MmscClient;
import com.company.agw.external.rcs.RcsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpamRecoveryService {

    private final AuthService authService;
    private final RequestValidator requestValidator;
    private final SpamMessageMapper spamMessageMapper;
    private final MmscClient mmscClient;
    private final RcsClient rcsClient;

    @Transactional
    public SpamRecoveryResponse recover(SpamRecoveryRequest request) {
        authService.authenticatePassRequest(request.getToken());
        requestValidator.requireText(request.getUserId(), "userId");
        requestValidator.requireText(request.getMessageId(), "messageId");

        SpamMessageEntity message = spamMessageMapper.selectSpamMessage(request.getUserId(), request.getMessageId());
        if (message == null) {
            throw new BusinessException(ResponseCode.MESSAGE_NOT_FOUND);
        }

        if (request.getMessageType() == MessageType.MMS) {
            mmscClient.recoverMms(request.getMessageId());
        } else if (request.getMessageType() == MessageType.RCS) {
            rcsClient.recoverRcs(request.getMessageId());
        }

        return SpamRecoveryResponse.builder()
                .messageId(request.getMessageId())
                .recoveryStatus("REQUESTED")
                .build();
    }
}
