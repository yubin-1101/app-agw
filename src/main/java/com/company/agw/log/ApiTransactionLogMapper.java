package com.company.agw.log;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiTransactionLogMapper {

    int insertApiTransactionLog(ApiTransactionLog apiTransactionLog);
}
