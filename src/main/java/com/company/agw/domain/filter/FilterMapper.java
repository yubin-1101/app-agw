package com.company.agw.domain.filter;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FilterMapper {

    List<FilterEntity> selectFilters(@Param("userId") String userId, @Param("filterType") FilterType filterType);

    int insertFilter(FilterEntity filter);

    int updateFilter(FilterEntity filter);

    int deleteFilter(@Param("filterSeq") Long filterSeq, @Param("userId") String userId);
}
