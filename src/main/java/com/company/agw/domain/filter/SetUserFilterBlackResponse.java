package com.company.agw.domain.filter;

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
        "blackNUM",
        "blackPattern",
        "blackPrefix"
})
public class SetUserFilterBlackResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;

    @JsonProperty("blackNUM")
    private List<List<Object>> blackNUM;

    private List<List<Object>> blackPattern;
    private List<List<Object>> blackPrefix;

    public static SetUserFilterBlackResponse success(
            String userID,
            List<List<Object>> blackNUM,
            List<List<Object>> blackPattern,
            List<List<Object>> blackPrefix
    ) {
        return SetUserFilterBlackResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("고객 정보 요청 성공하였습니다.")
                .blackNUM(blackNUM)
                .blackPattern(blackPattern)
                .blackPrefix(blackPrefix)
                .build();
    }

    public static SetUserFilterBlackResponse fail(String userID, Integer retCode, String retMsg) {
        return SetUserFilterBlackResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .blackNUM(List.of())
                .blackPattern(List.of())
                .blackPrefix(List.of())
                .build();
    }

    public static SetUserFilterBlackResponse notJoined() {
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
