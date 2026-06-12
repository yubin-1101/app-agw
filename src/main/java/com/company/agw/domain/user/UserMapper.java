package com.company.agw.domain.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    PassUserInfoEntity selectUserPrivateInfobyPass(@Param("decodeUserID") String decodeUserID);

    String getLastVisitAt(@Param("decodeUserID") String decodeUserID);

    int upsertLastVisitAt(@Param("decodeUserID") String decodeUserID);

    int updateUserInfoByPass(PassUserInfoEntity userInfo);
}
