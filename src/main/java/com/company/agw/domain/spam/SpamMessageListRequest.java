package com.company.agw.domain.spam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpamMessageListRequest {

    private String token;
    private String userId;
    private MessageType messageType;
    private Integer page;
    private Integer size;
}
