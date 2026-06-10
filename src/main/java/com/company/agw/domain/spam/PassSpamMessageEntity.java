package com.company.agw.domain.spam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassSpamMessageEntity {

    private String smsSeq;
    private String smsClc;
    private String cbNum;
    private String srcNum;
    private String smscSeq;
    private String smsKind;
    private String filterKind;
    private String spamWord;
    private String prvKind;
    private Integer smsLeng;
    private String smsMsg;
    private String cbUrl;
    private String custNum;
    private String sndKind;
    private String rcvDt;
    private String saveDt;
    private String smsMsgPk;
    private String dcsType;
    private Integer smsPkLeng;
    private Integer smsUpkLeng;
    private String imageFileName;
    private String spamPattern1;
    private String spamPattern2;
    private String spamPattern3;
    private String telId;
    private String msgOrigination;
}
