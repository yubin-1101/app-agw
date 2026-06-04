package com.company.agw.domain.spam;

import com.company.agw.auth.AuthService;
import com.company.agw.common.validation.RequestValidator;
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

    private final AuthService authService;
    private final RequestValidator requestValidator;
    private final SpamMessageMapper spamMessageMapper;

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
}
