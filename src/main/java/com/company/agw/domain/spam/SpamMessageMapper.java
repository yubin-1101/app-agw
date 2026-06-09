package com.company.agw.domain.spam;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SpamMessageMapper {

    List<SpamMessageEntity> selectSpamMessages(
            @Param("userId") String userId,
            @Param("messageType") MessageType messageType,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    List<PassSpamMessageEntity> selectSpamMessagesByPass(
            @Param("decodeUserID") String decodeUserID,
            @Param("maxMsgCnt") int maxMsgCnt
    );

    SpamMessageEntity selectSpamMessage(@Param("userId") String userId, @Param("messageId") String messageId);

    int updateDeleted(@Param("userId") String userId, @Param("messageId") String messageId);
}
