package com.company.agw.domain.filter;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterEntity {

    private Long filterSeq;
    private String userId;
    private FilterType filterType;
    private String filterValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
