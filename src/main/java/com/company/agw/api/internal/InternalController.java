package com.company.agw.api.internal;

import com.company.agw.auth.AuthService;
import com.company.agw.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    private final AuthService authService;

    @GetMapping("/health")
    public CommonResponse<String> health(@RequestHeader(value = "X-API-KEY", required = false) String apiKey) {
        authService.authenticateInternalRequest(apiKey);
        return CommonResponse.success("OK");
    }
}
