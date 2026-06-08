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
        "blackPrefix",
        "prefixPool"
})
public class GetUserFilterBlackResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;

    @JsonProperty("blackNUM")
    private List<List<Object>> blackNUM;

    private List<List<Object>> blackPattern;
    private List<List<Object>> blackPrefix;
    private List<List<Object>> prefixPool;

    public static GetUserFilterBlackResponse success(
            String userID,
            List<List<Object>> blackNUM,
            List<List<Object>> blackPattern,
            List<List<Object>> blackPrefix,
            List<List<Object>> prefixPool
    ) {
        return GetUserFilterBlackResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("고객 정보 요청 성공하였습니다.")
                .blackNUM(blackNUM)
                .blackPattern(blackPattern)
                .blackPrefix(blackPrefix)
                .prefixPool(prefixPool)
                .build();
    }

    public static GetUserFilterBlackResponse fail(String userID, Integer retCode, String retMsg) {
        return GetUserFilterBlackResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .blackNUM(List.of())
                .blackPattern(List.of())
                .blackPrefix(List.of())
                .prefixPool(List.of())
                .build();
    }

    public static GetUserFilterBlackResponse notJoined() {
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
