package com.company.agw.domain.spam;

import com.company.agw.common.response.PassResponseCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({
        "userID",
        "retCode",
        "retMsg",
        "spamListCnt",
        "spamMsgList"
})
public class GetSpamMsgListResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;
    private Integer spamListCnt;
    private List<List<Object>> spamMsgList;

    public static GetSpamMsgListResponse success(String userID, List<List<Object>> spamMsgList) {
        return GetSpamMsgListResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("고객 정보 요청 성공하였습니다.")
                .spamListCnt(spamMsgList.size())
                .spamMsgList(spamMsgList)
                .build();
    }

    public static GetSpamMsgListResponse fail(String userID, Integer retCode, String retMsg) {
        return GetSpamMsgListResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .spamListCnt(0)
                .spamMsgList(List.of())
                .build();
    }

    public static GetSpamMsgListResponse notJoined() {
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
