package com.company.agw.domain.keyword;

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
        "keywordInfoList"
})
public class GetKeywordInfoResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;
    private List<List<Object>> keywordInfoList;

    public static GetKeywordInfoResponse success(List<List<Object>> keywordInfoList) {
        return GetKeywordInfoResponse.builder()
                .userID("")
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("요청 처리 성공 하였습니다.")
                .keywordInfoList(keywordInfoList)
                .build();
    }

    public static GetKeywordInfoResponse fail(Integer retCode, String retMsg) {
        return GetKeywordInfoResponse.builder()
                .userID("")
                .retCode(retCode)
                .retMsg(retMsg)
                .keywordInfoList(List.of())
                .build();
    }
}
