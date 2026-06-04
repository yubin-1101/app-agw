package com.company.agw.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassUserInfoEntity {

    private String custNum;
    private Integer authKind;
    private Integer filterKind;
    private Integer emailKind;
    private String emailAddr1;
    private Integer addrFlag;
    private Integer kisaAgree;
    private String addrDt;
    private Integer cbSmsFlag;
    private String cbSmsTerm;
    private Integer cbSmsClc;
    private Integer dynmFlag;
    private String wifiId;
    private String wifiDate;
    private String msgDnTime;
    private String noticeDnTime;
    private Integer urlHoldoff;
    private Integer pushFlag;
    private String pushKey;
    private String platformVersion;
    private Integer fsecAgree;
    private Integer impersonateAgree;
    private Integer blockInternational;
    private Integer blockRoaming;
}
