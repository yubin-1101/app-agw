package com.company.agw.domain.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassSpamReportEntity {

    private String listSeq;
    private String spamType;
    private String srcNum;
    private String custNum;
    private String cbNum;
    private String rcvTime;
    private String msg;
    private String spamProbability;
    private String numberOfSc;
    private String status;
    private String emailFlag;
}
