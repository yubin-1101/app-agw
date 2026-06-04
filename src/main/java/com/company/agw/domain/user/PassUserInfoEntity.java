package com.company.agw.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassUserInfoEntity {

    private String custNum;
    private String authKind;
    private String filterKind;
    private String emailKind;
    private String emailAddr1;
    private String addrFlag;
    private String kisaAgree;
    private String addrDt;
    private String cbSmsFlag;
    private String cbSmsTerm;
    private String cbSmsClc;
    private String dynmFlag;
    private String wifiId;
    private String wifiDate;
    private String msgDnTime;
    private String noticeDnTime;
    private String urlHoldoff;
    private Integer pushFlag;
    private String pushKey;
    private String platformVersion;
    private String fsecAgree;
    private String impersonateAgree;
    private String blockInternational;
    private String blockRoaming;
}
