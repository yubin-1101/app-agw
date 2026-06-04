package com.company.agw.domain.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterListRequest {

    private String token;
    private String userId;
    private FilterType filterType;
}
