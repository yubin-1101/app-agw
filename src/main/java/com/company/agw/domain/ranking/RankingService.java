package com.company.agw.domain.ranking;

import com.company.agw.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final AuthService authService;
    private final RankingMapper rankingMapper;

    @Transactional(readOnly = true)
    public RankingResponse getRanking(RankingRequest request) {
        authService.authenticatePassRequest(request.getToken());
        return RankingResponse.builder()
                .keywords(rankingMapper.selectRankingKeywords(request.getRankingType()))
                .build();
    }
}
