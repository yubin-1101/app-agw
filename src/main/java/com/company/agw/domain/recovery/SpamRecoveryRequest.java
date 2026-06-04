package com.company.agw.domain.recovery;

import com.company.agw.domain.spam.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpamRecoveryRequest {

    private String token;
    private String userId;
    private String messageId;
    private MessageType messageType;
}
