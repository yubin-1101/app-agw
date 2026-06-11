package com.company.agw.domain.spam;

import com.company.agw.common.response.PassResponseCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({
        "userID",
        "retCode",
        "retMsg",
        "fileData"
})
public class GetDownloadFileResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;
    private List<Map<String, String>> fileData;

    public static GetDownloadFileResponse success(String userID, List<Map<String, String>> fileData) {
        return GetDownloadFileResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("요청 처리 성공 하였습니다.")
                .fileData(fileData)
                .build();
    }

    public static GetDownloadFileResponse fail(String userID, Integer retCode, String retMsg) {
        return GetDownloadFileResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .fileData(List.of())
                .build();
    }

    public static GetDownloadFileResponse notJoined() {
        return fail("", PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
