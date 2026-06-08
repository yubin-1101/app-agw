package com.company.agw.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetUserInfoRequest {

    @JsonProperty("userID")
    @Schema(description = "암호화된 사용자핸드폰 번호", writeOnly = true)
    private String userID;

    @Schema(
            description = "사용자 설정 상태. index: 0 E-Mail Forwarding, 1 운영자 스팸, 2 주소록 데이터, 3 KISA 보고 동의(0/1/2), 4 URL 스미싱 확인 후 전송, 5 Push 알리미, 6 금융보안원 공유, 7 사칭 문자 알림, 8 국제발신 차단, 9 로밍발신 차단",
            example = "[0,1,1,0,0,1,0,1,1,1]"
    )
    private List<Integer> userState;

    @Schema(description = "필터 가중치 값(0: 사용 안함, 2: 하, 3: 중, 4: 상)", example = "4")
    private Integer referenceFilterValue;
}
