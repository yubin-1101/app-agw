package com.company.agw.domain.filter;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FilterListResponse {

    private List<FilterResponse> filters;
}
