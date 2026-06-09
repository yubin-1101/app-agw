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
        "seqNO",
        "fileData"
})
public class GetSpamFileDataResponse {

    @JsonProperty("userID")
    private String userID;

    private Integer retCode;
    private String retMsg;
    private String seqNO;
    private List<Map<String, String>> fileData;

    public static GetSpamFileDataResponse success(String userID, String seqNO, List<Map<String, String>> fileData) {
        return GetSpamFileDataResponse.builder()
                .userID(userID)
                .retCode(PassResponseCode.SUCCESS.getRetCode())
                .retMsg("요청 처리 성공 하였습니다.")
                .seqNO(seqNO)
                .fileData(fileData)
                .build();
    }

    public static GetSpamFileDataResponse fail(String userID, String seqNO, Integer retCode, String retMsg) {
        return GetSpamFileDataResponse.builder()
                .userID(userID == null ? "" : userID)
                .retCode(retCode)
                .retMsg(retMsg)
                .seqNO(seqNO == null ? "" : seqNO)
                .fileData(List.of())
                .build();
    }

    public static GetSpamFileDataResponse notJoined(String seqNO) {
        return fail("", seqNO, PassResponseCode.NOT_JOINED.getRetCode(), PassResponseCode.NOT_JOINED.getRetMsg());
    }
}
