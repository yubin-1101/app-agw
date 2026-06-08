package com.company.agw.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserInfoRequest {

    @JsonProperty("userID")
    @Schema(description = "암호화된 사용자핸드폰 번호", writeOnly = true)
    private String userID;
}
