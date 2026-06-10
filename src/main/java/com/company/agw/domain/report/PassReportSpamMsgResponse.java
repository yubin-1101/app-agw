package com.company.agw.domain.report;

import com.company.agw.common.response.PassResponseCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({
        "userID",
        "retCode",
        "retMsg"
})
public class PassReportSpamMsgResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;

    public static PassReportSpamMsgResponse success(String userID) {
        return PassReportSpamMsgResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("요청 처리 성공하였습니다.")
                .build();
    }

    public static PassReportSpamMsgResponse fail(String userID, Integer retCode, String retMsg) {
        return PassReportSpamMsgResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .build();
    }

    public static PassReportSpamMsgResponse notJoined() {
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
