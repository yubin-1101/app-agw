package com.company.agw.domain.spam;

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
public class RemoveSpamMsgResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;

    public static RemoveSpamMsgResponse success(String userID) {
        return RemoveSpamMsgResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("요청 처리 성공 하였습니다")
                .build();
    }

    public static RemoveSpamMsgResponse fail(String userID, Integer retCode, String retMsg) {
        return RemoveSpamMsgResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .build();
    }

    public static RemoveSpamMsgResponse notJoined() {
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
