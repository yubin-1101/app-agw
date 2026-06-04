package com.company.agw.domain.filter;

import com.company.agw.auth.AuthService;
import com.company.agw.common.validation.RequestValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final AuthService authService;
    private final RequestValidator requestValidator;
    private final FilterMapper filterMapper;

    @Transactional(readOnly = true)
    public FilterListResponse getFilters(FilterListRequest request) {
        authService.authenticatePassRequest(request.getToken());
        requestValidator.requireText(request.getUserId(), "userId");

        List<FilterResponse> filters = filterMapper.selectFilters(request.getUserId(), request.getFilterType())
                .stream()
                .map(FilterResponse::from)
                .toList();

        return FilterListResponse.builder()
                .filters(filters)
                .build();
    }
}
