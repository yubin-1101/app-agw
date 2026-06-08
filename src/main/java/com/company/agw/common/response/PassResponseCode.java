package com.company.agw.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PassResponseCode {

    SUCCESS(1000, "API 요청 처리가 성공"),
    USER_NOT_FOUND(1001, "사용자 정보 없음"),
    MMS_EXPIRED(1002, "MMS 보관 주기 만기"),
    UAMP_ERROR(1010, "uAMP 서버 연동 에러"),
    DB_ERROR(1011, "DB 서버 연동 에러"),
    SMSS_ERROR(1012, "V-SMSS서버 연동 에러"),
    MMSC_ERROR(1013, "MMSC 서버 연동 에러"),
    UNSUPPORTED_FEATURE(1014, "미지원 기능"),
    AUTH_KEY_CREATE_ERROR(1015, "인증키 생성 오류"),
    KEY_AUTH_SUCCESS(1100, "Key 인증 처리 성공"),
    INVALID_PARAMETER(1400, "잘못된 파라미터 값 전달"),
    IP_AUTH_ERROR(1401, "IP인증 에러"),
    KEY_AUTH_FAILED_1(1402, "Key 인증 실패"),
    KEY_AUTH_FAILED_2(1403, "Key인증 실패"),
    IMAGE_DECRYPT_ERROR(1410, "이미지 복호화 에러"),
    DELETE_MESSAGE_NOT_FOUND(1411, "삭제할 메세지가 없습니다."),
    PROCESS_ERROR(1500, "처리 오류가 발생 하였습니다. 다시 시도 하여 주십시오"),
    RESTORE_EXPIRED(1510, "복원 유효 기간 오류"),
    IMAGE_EXPIRED(1511, "이미지 보관 기간 오류"),
    RESTORE_MESSAGE_NOT_FOUND(1512, "복원 메시지 오류"),
    REPORT_EXPIRED(1513, "보관 기간이 지나 신고할 수 없습니다"),
    REPORT_MESSAGE_NOT_FOUND(1514, "신고 할 메시지가 없습니다"),
    TRAFFIC_DELAY(1600, "사용량이 많아 지연되고 있습니다. 잠시 후에 이용하여 주십시오."),
    NOT_JOINED(1610, "스팸필터링 서비스 가입 후에 사용 가능합니다"),
    AUTH_FAILED_RETRY(1611, "인증이 실패 되었습니다. 잠시 후 다시 시도 바랍니다."),
    AUTH_FAILED_NETWORK(1612, "인증이 실패 되었습니다. SKT 통신망 상태에서 재 접속 해주세요."),
    SKT_ONLY(1613, "SK텔레콤 가입자만 사용 가능합니다"),
    AUTH_INFO_MISMATCH(1614, "인증 정보가 불일치하여 실패 되었습니다. 잠시 후 다시 시도 바랍니다.");

    private final int retCode;
    private final String retMsg;
}
