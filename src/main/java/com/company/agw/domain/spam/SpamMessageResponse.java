package com.company.agw.domain.spam;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpamMessageResponse {

    private String messageId;
    private MessageType messageType;
    private String sender;
    private String preview;
    private LocalDateTime receivedAt;

    public static SpamMessageResponse from(SpamMessageEntity entity) {
        return SpamMessageResponse.builder()
                .messageId(entity.getMessageId())
                .messageType(entity.getMessageType())
                .sender(entity.getSender())
                .preview(entity.getPreview())
                .receivedAt(entity.getReceivedAt())
                .build();
    }
}
