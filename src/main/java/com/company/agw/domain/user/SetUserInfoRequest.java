package com.company.agw.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetUserInfoRequest {

    @JsonProperty("userID")
    @Schema(description = "암호화된 사용자핸드폰 번호", writeOnly = true)
    private String userID;

    @Schema(description = "사용자 설정 상태")
    private List<Integer> userState;

    @Schema(description = "필터 가중치 값")
    private Integer referenceFilterValue;
}
