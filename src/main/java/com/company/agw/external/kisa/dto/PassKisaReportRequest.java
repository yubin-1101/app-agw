package com.company.agw.external.kisa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PassKisaReportRequest {

    @JsonProperty("userID")
    private String userID;

    private String smsType;
    private String smsSeq;
    private String reportData;
}
