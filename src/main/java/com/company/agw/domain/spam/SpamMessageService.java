package com.company.agw.domain.spam;

import com.company.agw.auth.AuthService;
import com.company.agw.common.response.PassResponseCode;
import com.company.agw.common.validation.RequestValidator;
import com.company.agw.domain.user.UserMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpamMessageService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final int PASS_MAX_SIZE = 200;

    private final AuthService authService;
    private final RequestValidator requestValidator;
    private final SpamMessageMapper spamMessageMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public SpamMessageListResponse getSpamMessages(SpamMessageListRequest request) {
        authService.authenticatePassRequest(request.getToken());
        requestValidator.requireText(request.getUserId(), "userId");

        int page = request.getPage() == null || request.getPage() < 0 ? DEFAULT_PAGE : request.getPage();
        int size = request.getSize() == null || request.getSize() < 1 ? DEFAULT_SIZE : Math.min(request.getSize(), MAX_SIZE);
        int offset = page * size;

        List<SpamMessageResponse> messages = spamMessageMapper
                .selectSpamMessages(request.getUserId(), request.getMessageType(), offset, size)
                .stream()
                .map(SpamMessageResponse::from)
                .toList();

        return SpamMessageListResponse.builder()
                .messages(messages)
                .build();
    }

    @Transactional(readOnly = true)
    public GetSpamMsgListResponse getSpamMsgList(GetSpamMsgListRequest request) {
        String userID = request == null ? null : request.getUserID();
        String decodeUserID;

        try {
            decodeUserID = authService.decryptPassUserId(userID);
        } catch (Exception e) {
            return GetSpamMsgListResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!hasText(userID) || !isNumeric(decodeUserID)) {
            return GetSpamMsgListResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (userMapper.selectUserPrivateInfobyPass(decodeUserID) == null) {
                return GetSpamMsgListResponse.notJoined();
            }

            List<List<Object>> spamMsgList = spamMessageMapper.selectSpamMessagesByPass(decodeUserID, PASS_MAX_SIZE)
                    .stream()
                    .map(this::toPassSpamMessageRow)
                    .toList();

            return GetSpamMsgListResponse.success(userID, spamMsgList);
        } catch (Exception e) {
            return GetSpamMsgListResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    private List<Object> toPassSpamMessageRow(PassSpamMessageEntity entity) {
        String smsClc = defaultText(entity.getSmsClc());
        List<Object> row = new ArrayList<>();
        row.add(defaultText(entity.getSmsSeq()));
        row.add(defaultText(entity.getSmsKind()));
        row.add(defaultText(entity.getCbNum()));
        row.add(toPassDate(smsClc));
        row.add(toPassTime(smsClc));
        row.add(defaultText(entity.getCbUrl()));
        row.add(defaultText(entity.getFilterKind()));
        row.add(toPassBlockReason(entity.getFilterKind(), entity.getSpamWord()));
        row.add(defaultText(entity.getSmsMsg()));
        row.add(defaultText(entity.getImageFileName()));
        return row;
    }

    private String toPassDate(String smsClc) {
        if (smsClc.length() < 8) {
            return "";
        }

        return smsClc.substring(0, 8);
    }

    private String toPassTime(String smsClc) {
        if (smsClc.length() < 14) {
            return smsClc.length() >= 8 ? smsClc.substring(8) : "";
        }

        return smsClc.substring(8, 14);
    }

    private String toPassBlockReason(String filterKind, String spamWord) {
        if ("P".equals(filterKind)) {
            return "경찰청 차단";
        }

        if ("b".equals(filterKind) || "c".equals(filterKind) || "d".equals(filterKind)) {
            return defaultText(spamWord);
        }

        return "운영자 지능형 스팸 차단";
    }

    private String defaultText(String value) {
        return value == null ? "" : value;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isNumeric(String value) {
        return hasText(value) && value.chars().allMatch(Character::isDigit);
    }
}
