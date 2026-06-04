package com.company.agw;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.agw.common.response.CommonResponse;
import org.junit.jupiter.api.Test;

class AgwApplicationTests {

    @Test
    void commonResponseSuccess() {
        CommonResponse<String> response = CommonResponse.success("OK");

        assertThat(response.getRetCode()).isEqualTo("0000");
        assertThat(response.getRetMsg()).isEqualTo("SUCCESS");
        assertThat(response.getData()).isEqualTo("OK");
    }
}
