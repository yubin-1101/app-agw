package com.company.agw.log;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserActionLogMapper {

    int insertUserActionLog(UserActionLog userActionLog);
}
