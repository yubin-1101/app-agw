package com.company.agw.domain.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetUserFilterBlackRequest {

    @JsonProperty("userID")
    @Schema(description = "암호화된 사용자핸드폰 번호", writeOnly = true)
    private String userID;

    @JsonProperty("blackNUM")
    @Schema(
            description = "번호 차단 목록. row format: [cmdType, ID, data, category, memo], cmdType 1: 추가, 2: 삭제, 3: 변경, data 최대 12Byte",
            writeOnly = true,
            example = "[[1,\"\",\"0102099999\",\"광고\",\"회사메세\"]]"
    )
    private List<List<Object>> blackNUM = new ArrayList<>();

    @Schema(
            description = "문구 차단 목록. row format: [cmdType, ID, data, category, memo], cmdType 1: 추가, 2: 삭제, 3: 변경, data 최대 15Byte",
            writeOnly = true,
            example = "[]"
    )
    private List<List<Object>> blackPattern = new ArrayList<>();

    @Schema(
            description = "국번 차단 목록. row format: [cmdType, ID, data, category, memo], cmdType 1: 추가, 2: 삭제, 3: 변경, data 최대 12Byte",
            writeOnly = true,
            example = "[]"
    )
    private List<List<Object>> blackPrefix = new ArrayList<>();
}
