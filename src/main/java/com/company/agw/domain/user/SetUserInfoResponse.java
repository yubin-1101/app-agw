package com.company.agw.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SetUserInfoResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;

    public static SetUserInfoResponse success(String userID) {
        return SetUserInfoResponse.builder()
                .userID(userID)
                .retCode(1000)
                .retMsg("고객 정보 요청 성공하였습니다.")
                .build();
    }

    public static SetUserInfoResponse fail(String userID, Integer retCode, String retMsg) {
        return SetUserInfoResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .build();
    }

    public static SetUserInfoResponse notJoined() {
        return fail("", 1610, "스팸필터링 서비스 가입 후에 사용 가능합니다.");
    }
}
