package com.company.agw.domain.ranking;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RankingMapper {

    List<String> selectRankingKeywords(@Param("rankingType") String rankingType);
}
