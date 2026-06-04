package com.company.agw.domain.ranking;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingResponse {

    private List<String> keywords;
}
