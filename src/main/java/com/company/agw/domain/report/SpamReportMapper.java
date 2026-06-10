package com.company.agw.domain.report;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SpamReportMapper {

    int insertReportHistory(SpamReportEntity report);

    int insertKisaSktMessage(PassSpamReportEntity report);

    int insertPassReportSpamMsgHistory(
            @Param("custNum") String custNum,
            @Param("rst") String rst,
            @Param("jobMsg") String jobMsg
    );
}
