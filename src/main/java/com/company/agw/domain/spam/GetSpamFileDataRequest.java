package com.company.agw.domain.spam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSpamFileDataRequest {

    @JsonProperty("userID")
    private String userID;

    private String msgType;
    private String seqNO;
    private String fileName;
}
