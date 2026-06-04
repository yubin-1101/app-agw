package com.company.agw.domain.filter;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FilterResponse {

    private Long filterSeq;
    private FilterType filterType;
    private String filterValue;

    public static FilterResponse from(FilterEntity entity) {
        return FilterResponse.builder()
                .filterSeq(entity.getFilterSeq())
                .filterType(entity.getFilterType())
                .filterValue(entity.getFilterValue())
                .build();
    }
}
