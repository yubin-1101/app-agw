package com.company.agw.domain.recovery;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpamRecoveryResponse {

    private String messageId;
    private String recoveryStatus;
}
