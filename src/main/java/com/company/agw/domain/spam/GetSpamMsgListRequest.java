package com.company.agw.domain.spam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSpamMsgListRequest {

    @JsonProperty("userID")
    private String userID;
}
