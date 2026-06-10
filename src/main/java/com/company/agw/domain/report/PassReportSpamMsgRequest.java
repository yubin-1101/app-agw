package com.company.agw.domain.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassReportSpamMsgRequest {

    @JsonProperty("userID")
    private String userID;

    private String smsType;
    private String smsSeq;
    private String reportData;
}
