package com.company.agw.domain.filter;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FilterMapper {

    List<FilterEntity> selectFilters(@Param("userId") String userId, @Param("filterType") FilterType filterType);

    List<PassFilterRowEntity> selectWhiteFiltersByPass(@Param("decodeUserID") String decodeUserID);

    List<PassFilterRowEntity> selectBlackFiltersByPass(@Param("decodeUserID") String decodeUserID);

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

    int countBlackNumberByData(
            @Param("custNum") String custNum,
            @Param("data") String data,
            @Param("excludeId") String excludeId
    );

    int countBlackPatternByData(
            @Param("custNum") String custNum,
            @Param("data") String data,
            @Param("excludeId") String excludeId
    );

    int countBlackPrefixByData(
            @Param("custNum") String custNum,
            @Param("data") String data,
            @Param("excludeId") String excludeId
    );

    int insertWhiteNumber(PassFilterCommandEntity entity);

    int updateWhiteNumber(PassFilterCommandEntity entity);

    int deleteWhiteNumber(@Param("custNum") String custNum, @Param("id") String id);

    int insertWhitePattern(PassFilterCommandEntity entity);

    int updateWhitePattern(PassFilterCommandEntity entity);

    int deleteWhitePattern(@Param("custNum") String custNum, @Param("id") String id);

    int insertWhiteAddress(PassFilterCommandEntity entity);

    int updateWhiteAddress(PassFilterCommandEntity entity);

    int deleteWhiteAddress(@Param("custNum") String custNum, @Param("id") String id);

    int insertBlackNumber(PassFilterCommandEntity entity);

    int updateBlackNumber(PassFilterCommandEntity entity);

    int deleteBlackNumber(@Param("custNum") String custNum, @Param("id") String id);

    int insertBlackPattern(PassFilterCommandEntity entity);

    int updateBlackPattern(PassFilterCommandEntity entity);

    int deleteBlackPattern(@Param("custNum") String custNum, @Param("id") String id);

    int insertBlackPrefix(PassFilterCommandEntity entity);

    int updateBlackPrefix(PassFilterCommandEntity entity);

    int deleteBlackPrefix(@Param("custNum") String custNum, @Param("id") String id);

    int insertFilter(FilterEntity filter);

    int updateFilter(FilterEntity filter);

    int deleteFilter(@Param("filterSeq") Long filterSeq, @Param("userId") String userId);
}
