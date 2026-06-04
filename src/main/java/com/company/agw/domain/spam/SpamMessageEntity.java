package com.company.agw.domain.spam;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpamMessageEntity {

    private Long spamSeq;
    private String messageId;
    private String userId;
    private MessageType messageType;
    private String sender;
    private String preview;
    private String deletedYn;
    private LocalDateTime receivedAt;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
