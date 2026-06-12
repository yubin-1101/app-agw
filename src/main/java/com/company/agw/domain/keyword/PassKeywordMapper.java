package com.company.agw.domain.keyword;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PassKeywordMapper {

    List<PassKeywordEntity> selectPassKeywords(
            @Param("channel") String channel,
            @Param("savedt") String savedt,
            @Param("themeCode") String themeCode
    );
}
