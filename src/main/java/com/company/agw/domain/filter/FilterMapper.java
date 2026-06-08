package com.company.agw.domain.filter;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FilterMapper {

    List<FilterEntity> selectFilters(@Param("userId") String userId, @Param("filterType") FilterType filterType);

    List<UserWhiteFilterEntity> selectWhiteFiltersByPass(@Param("decodeUserID") String decodeUserID);

    int countWhiteNumberByData(
            @Param("custNum") String custNum,
            @Param("data") String data,
            @Param("excludeId") String excludeId
    );

    int countWhitePatternByData(
            @Param("custNum") String custNum,
            @Param("data") String data,
            @Param("excludeId") String excludeId
    );

    int countWhiteAddressByData(
            @Param("custNum") String custNum,
            @Param("data") String data,
            @Param("excludeId") String excludeId
    );

    int insertWhiteNumber(UserWhiteFilterCommandEntity entity);

    int updateWhiteNumber(UserWhiteFilterCommandEntity entity);

    int deleteWhiteNumber(@Param("custNum") String custNum, @Param("id") String id);

    int insertWhitePattern(UserWhiteFilterCommandEntity entity);

    int updateWhitePattern(UserWhiteFilterCommandEntity entity);

    int deleteWhitePattern(@Param("custNum") String custNum, @Param("id") String id);

    int insertWhiteAddress(UserWhiteFilterCommandEntity entity);

    int updateWhiteAddress(UserWhiteFilterCommandEntity entity);

    int deleteWhiteAddress(@Param("custNum") String custNum, @Param("id") String id);

    int insertFilter(FilterEntity filter);

    int updateFilter(FilterEntity filter);

    int deleteFilter(@Param("filterSeq") Long filterSeq, @Param("userId") String userId);
}
