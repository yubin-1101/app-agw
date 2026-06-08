package com.company.agw.domain.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.company.agw.common.response.PassResponseCode;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({
        "userID",
        "retCode",
        "retMsg",
        "whiteNUM",
        "whitePattern",
        "whiteNUMAddr"
})
public class GetUserFilterWhiteResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;

    @JsonProperty("whiteNUM")
    private List<List<Object>> whiteNUM;

    private List<List<Object>> whitePattern;

    @JsonProperty("whiteNUMAddr")
    private List<List<Object>> whiteNUMAddr;

    public static GetUserFilterWhiteResponse success(
            String userID,
            List<List<Object>> whiteNUM,
            List<List<Object>> whitePattern,
            List<List<Object>> whiteNUMAddr
    ) {
        return GetUserFilterWhiteResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("고객 정보 요청 성공하였습니다.")
                .whiteNUM(whiteNUM)
                .whitePattern(whitePattern)
                .whiteNUMAddr(whiteNUMAddr)
                .build();
    }

    public static GetUserFilterWhiteResponse fail(String userID, Integer retCode, String retMsg) {
        return GetUserFilterWhiteResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .whiteNUM(List.of())
                .whitePattern(List.of())
                .whiteNUMAddr(List.of())
                .build();
    }

    public static GetUserFilterWhiteResponse notJoined() {
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
