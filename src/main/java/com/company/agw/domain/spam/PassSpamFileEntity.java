package com.company.agw.domain.spam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassSpamFileEntity {

    private String smsSeq;
    private String imageDecodingKey;
    private String saveDt;
    private String imageFileName;
    private String cbNum;
}
