package com.company.agw.domain.user;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEntity {

    private Long userSeq;
    private String userId;
    private String mdn;
    private String serviceStatus;
    private String settingValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
