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
        "retMsg",
        "seqNO"
})
public class RecoverySpamMsgResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;
    private String seqNO;

    public static RecoverySpamMsgResponse success(String userID, String seqNO) {
        return RecoverySpamMsgResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("가입자 메시지 복구요청 성공하였습니다.")
                .seqNO(seqNO)
                .build();
    }

    public static RecoverySpamMsgResponse fail(String userID, String seqNO, Integer retCode, String retMsg) {
        return RecoverySpamMsgResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .seqNO(seqNO == null ? "" : seqNO)
                .build();
    }

    public static RecoverySpamMsgResponse notJoined(String seqNO) {
        return fail("", seqNO, PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
