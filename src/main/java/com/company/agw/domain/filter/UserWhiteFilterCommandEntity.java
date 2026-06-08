package com.company.agw.domain.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWhiteFilterCommandEntity {

    private String id;
    private String custNum;
    private String data;
    private String category;
    private String memo;
    private String saveDt;
}
