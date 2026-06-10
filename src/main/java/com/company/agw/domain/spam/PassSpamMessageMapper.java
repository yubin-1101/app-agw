package com.company.agw.domain.spam;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PassSpamMessageMapper {

    List<PassSpamMessageEntity> selectSpamMessagesByPass(
            @Param("decodeUserID") String decodeUserID,
            @Param("maxMsgCnt") int maxMsgCnt
    );

    PassSpamFileEntity selectSpamFileByPass(
            @Param("decodeUserID") String decodeUserID,
            @Param("seqNO") String seqNO
    );

    String selectTelIdByPass(
            @Param("decodeUserID") String decodeUserID,
            @Param("seqNO") String seqNO
    );

    PassSpamMessageEntity selectRecoverySpamSMS(
            @Param("custNum") String custNum,
            @Param("msgType") String msgType,
            @Param("seqNO") String seqNO
    );

    PassSpamMessageEntity getTelIdFromSmsSeq(
            @Param("custNum") String custNum,
            @Param("seqNO") String seqNO
    );

    int insertMfsRecoverMessage(
            @Param("msgKey") String msgKey,
            @Param("msgType") String msgType,
            @Param("custNum") String custNum
    );

    int deleteSpamMessageBySeqNo(@Param("seqNO") String seqNO);

    int deleteSpamMessagesByCustNum(@Param("decodeUserID") String decodeUserID);

    int insertRemoveSpamMsgHistory(
            @Param("custNum") String custNum,
            @Param("rst") String rst,
            @Param("jobMsg") String jobMsg
    );

    int insertRecoverySpamMsgHistory(
            @Param("custNum") String custNum,
            @Param("rst") String rst,
            @Param("jobMsg") String jobMsg
    );
}
