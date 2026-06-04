package com.company.agw.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusRequest {

    private String token;
    private String userId;
    private String mdn;
}
