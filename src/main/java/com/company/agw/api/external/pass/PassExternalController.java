package com.company.agw.api.external.pass;

import com.company.agw.common.response.CommonResponse;
import com.company.agw.domain.filter.FilterService;
import com.company.agw.domain.filter.GetUserFilterBlackRequest;
import com.company.agw.domain.filter.GetUserFilterBlackResponse;
import com.company.agw.domain.filter.GetUserFilterWhiteRequest;
import com.company.agw.domain.filter.GetUserFilterWhiteResponse;
import com.company.agw.domain.filter.SetUserFilterBlackRequest;
import com.company.agw.domain.filter.SetUserFilterBlackResponse;
import com.company.agw.domain.filter.SetUserFilterWhiteRequest;
import com.company.agw.domain.filter.SetUserFilterWhiteResponse;
import com.company.agw.domain.keyword.GetKeywordInfoRequest;
import com.company.agw.domain.keyword.GetKeywordInfoResponse;
import com.company.agw.domain.keyword.PassKeywordService;
import com.company.agw.domain.report.PassReportSpamMsgRequest;
import com.company.agw.domain.report.PassReportSpamMsgResponse;
import com.company.agw.domain.report.PassSpamReportService;
import com.company.agw.domain.spam.GetDownloadFileRequest;
import com.company.agw.domain.spam.GetDownloadFileResponse;
import com.company.agw.domain.spam.GetSpamFileDataRequest;
import com.company.agw.domain.spam.GetSpamFileDataResponse;
import com.company.agw.domain.spam.GetSpamMsgListRequest;
import com.company.agw.domain.spam.GetSpamMsgListResponse;
import com.company.agw.domain.spam.PassSpamMessageService;
import com.company.agw.domain.spam.RecoverySpamMsgRequest;
import com.company.agw.domain.spam.RecoverySpamMsgResponse;
import com.company.agw.domain.spam.RemoveSpamMsgRequest;
import com.company.agw.domain.spam.RemoveSpamMsgResponse;
import com.company.agw.domain.spam.SpamMessageListRequest;
import com.company.agw.domain.spam.SpamMessageListResponse;
import com.company.agw.domain.spam.SpamMessageService;
import com.company.agw.domain.user.GetUserInfoRequest;
import com.company.agw.domain.user.GetUserInfoResponse;
import com.company.agw.domain.user.SetUserInfoRequest;
import com.company.agw.domain.user.SetUserInfoResponse;
import com.company.agw.domain.user.UserService;
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
    private final PassSpamMessageService passSpamMessageService;
    private final PassSpamReportService passSpamReportService;
    private final PassKeywordService passKeywordService;
    private final SpamMessageService spamMessageService;

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

    @AopLogInfo(menuPath = "PASS > 스팸 메시지 > 목록 조회", action = LogAction.SEARCH)
    @PostMapping("/v1/getSpamMsgList")
    public GetSpamMsgListResponse getSpamMsgList(
            @RequestBody GetSpamMsgListRequest request,
            HttpServletRequest httpRequest
    ) {
        GetSpamMsgListResponse response = passSpamMessageService.getSpamMsgList(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 스팸 메시지 > 파일 다운로드", action = LogAction.SEARCH)
    @PostMapping("/v1/getSpamFileData")
    public GetSpamFileDataResponse getSpamFileData(
            @RequestBody GetSpamFileDataRequest request,
            HttpServletRequest httpRequest
    ) {
        GetSpamFileDataResponse response = passSpamMessageService.getSpamFileData(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 스팸 메시지 > RCS 첨부파일 다운로드", action = LogAction.SEARCH)
    @PostMapping("/v1/getDownloadFile")
    public GetDownloadFileResponse getDownloadFile(
            @RequestBody GetDownloadFileRequest request,
            HttpServletRequest httpRequest
    ) {
        GetDownloadFileResponse response = passSpamMessageService.getDownloadFile(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 스팸 메시지 > 삭제", action = LogAction.DELETE)
    @PostMapping("/v1/removeSpamMsg")
    public RemoveSpamMsgResponse removeSpamMsg(
            @RequestBody RemoveSpamMsgRequest request,
            HttpServletRequest httpRequest
    ) {
        RemoveSpamMsgResponse response = passSpamMessageService.removeSpamMsg(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 스팸 메시지 > 복구", action = LogAction.UPDATE)
    @PostMapping("/v1/recoverySpamMsg")
    public RecoverySpamMsgResponse recoverySpamMsg(
            @RequestBody RecoverySpamMsgRequest request,
            HttpServletRequest httpRequest
    ) {
        RecoverySpamMsgResponse response = passSpamMessageService.recoverySpamMsg(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 스팸 메시지 > 간편 신고", action = LogAction.CREATE)
    @PostMapping("/v1/reportSpamMsg")
    public PassReportSpamMsgResponse reportSpamMsg(
            @RequestBody PassReportSpamMsgRequest request,
            HttpServletRequest httpRequest
    ) {
        PassReportSpamMsgResponse response = passSpamReportService.reportSpamMsg(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", request == null ? null : request.getUserID());
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 키워드 > 조회", action = LogAction.SEARCH)
    @PostMapping("/v1/getKeywordInfo")
    public GetKeywordInfoResponse getKeywordInfo(
            @RequestBody(required = false) GetKeywordInfoRequest request,
            HttpServletRequest httpRequest
    ) {
        GetKeywordInfoResponse response = passKeywordService.getKeywordInfo(request);
        httpRequest.setAttribute("retCode", String.valueOf(response.getRetCode()));
        httpRequest.setAttribute("retMsg", response.getRetMsg());
        httpRequest.setAttribute("userID", "");
        return response;
    }

    @AopLogInfo(menuPath = "PASS > 스팸 메시지 > 목록 조회", action = LogAction.SEARCH)
    @PostMapping("/spam/messages")
    public CommonResponse<SpamMessageListResponse> getSpamMessages(@RequestBody SpamMessageListRequest request) {
        return CommonResponse.success(spamMessageService.getSpamMessages(request));
    }
}
