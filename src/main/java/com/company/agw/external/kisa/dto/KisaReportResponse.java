package com.company.agw.external.kisa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KisaReportResponse {

    private String messageId;
    private String resultCode;
    private String resultMessage;
    private String transactionId;
}
