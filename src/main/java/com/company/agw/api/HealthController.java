package com.company.agw.api;

import com.company.agw.common.response.CommonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public CommonResponse<String> health() {
        return CommonResponse.success("OK");
    }
}
