package com.company.agw.domain.report;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpamReportMapper {

    int insertReportHistory(SpamReportEntity report);
}
