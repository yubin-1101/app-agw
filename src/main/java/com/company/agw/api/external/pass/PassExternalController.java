package com.company.agw.api.external.pass;

import com.company.agw.common.response.CommonResponse;
import com.company.agw.domain.filter.FilterListRequest;
import com.company.agw.domain.filter.FilterListResponse;
import com.company.agw.domain.filter.FilterService;
import com.company.agw.domain.filter.GetUserFilterBlackRequest;
import com.company.agw.domain.filter.GetUserFilterBlackResponse;
import com.company.agw.domain.filter.GetUserFilterWhiteRequest;
import com.company.agw.domain.filter.GetUserFilterWhiteResponse;
import com.company.agw.domain.filter.SetUserFilterBlackRequest;
import com.company.agw.domain.filter.SetUserFilterBlackResponse;
import com.company.agw.domain.filter.SetUserFilterWhiteRequest;
import com.company.agw.domain.filter.SetUserFilterWhiteResponse;
import com.company.agw.domain.spam.SpamMessageListRequest;
import com.company.agw.domain.spam.SpamMessageListResponse;
import com.company.agw.domain.spam.SpamMessageService;
import com.company.agw.domain.user.GetUserInfoRequest;
import com.company.agw.domain.user.GetUserInfoResponse;
import com.company.agw.domain.user.SetUserInfoRequest;
import com.company.agw.domain.user.SetUserInfoResponse;
import com.company.agw.domain.user.UserService;
import com.company.agw.domain.user.UserStatusRequest;
import com.company.agw.domain.user.UserStatusResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.company.agw.log.AopLogInfo;
import com.company.agw.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/pass")
@RequiredArgsConstructor
public class PassExternalController {

    private final UserService userService;
    private final FilterService filterService;
    private final SpamMessageService spamMessageService;

    @AopLogInfo(menuPath = "PASS > 사용자 > 상태 조회", action = LogAction.SEARCH)
    @PostMapping("/user/status")
    public CommonResponse<UserStatusResponse> getUserStatus(@RequestBody UserStatusRequest request) {
        return CommonResponse.success(userService.getUserStatus(request));
    }

    @AopLogInfo(menuPath = "PASS > 사용자 > 정보 조회", action = LogAction.SEARCH)
    @PostMapping("/v1/getUserInfo")
    public GetUserInfoResponse getUserInfo(@RequestBody GetUserInfoRequest request, HttpServletRequest httpRequest) {
        GetUserInfoResponse response = userService.getUserInfo(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        httpRequest.setAttribute("mdn", response.getUserInfo());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 사용자 > 설정정보 변경", action = LogAction.UPDATE)
    @PostMapping("/v1/setUserInfo")
    public SetUserInfoResponse setUserInfo(@RequestBody SetUserInfoRequest request, HttpServletRequest httpRequest) {
        SetUserInfoResponse response = userService.setUserInfo(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 필터 > 화이트리스트 조회", action = LogAction.SEARCH)
    @PostMapping("/v1/getUserfilterWhite")
    public GetUserFilterWhiteResponse getUserFilterWhite(
            @RequestBody GetUserFilterWhiteRequest request,
            HttpServletRequest httpRequest
    ) {
        GetUserFilterWhiteResponse response = filterService.getUserFilterWhite(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 필터 > 블랙리스트 조회", action = LogAction.SEARCH)
    @PostMapping("/v1/getUserfilterBlack")
    public GetUserFilterBlackResponse getUserFilterBlack(
            @RequestBody GetUserFilterBlackRequest request,
            HttpServletRequest httpRequest
    ) {
        GetUserFilterBlackResponse response = filterService.getUserFilterBlack(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 필터 > 화이트리스트 설정", action = LogAction.UPDATE)
    @PostMapping("/v1/setUserfilterWhite")
    public SetUserFilterWhiteResponse setUserFilterWhite(
            @RequestBody SetUserFilterWhiteRequest request,
            HttpServletRequest httpRequest
    ) {
        SetUserFilterWhiteResponse response = filterService.setUserFilterWhite(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 필터 > 블랙리스트 설정", action = LogAction.UPDATE)
    @PostMapping("/v1/setUserfilterBlack")
    public SetUserFilterBlackResponse setUserFilterBlack(
            @RequestBody SetUserFilterBlackRequest request,
            HttpServletRequest httpRequest
    ) {
        SetUserFilterBlackResponse response = filterService.setUserFilterBlack(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 필터 > 목록 조회", action = LogAction.SEARCH)
    @PostMapping("/filters")
    public CommonResponse<FilterListResponse> getFilters(@RequestBody FilterListRequest request) {
        return CommonResponse.success(filterService.getFilters(request));
    }

    @AopLogInfo(menuPath = "PASS > 스팸 메시지 > 목록 조회", action = LogAction.SEARCH)
    @PostMapping("/spam/messages")
    public CommonResponse<SpamMessageListResponse> getSpamMessages(@RequestBody SpamMessageListRequest request) {
        return CommonResponse.success(spamMessageService.getSpamMessages(request));
    }
}
