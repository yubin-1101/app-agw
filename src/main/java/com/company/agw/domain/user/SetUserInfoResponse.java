package com.company.agw.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.company.agw.common.response.PassResponseCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({
        "userID",
        "retCode",
        "retMsg"
})
public class SetUserInfoResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;

    public static SetUserInfoResponse success(String userID) {
        return SetUserInfoResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
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
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
