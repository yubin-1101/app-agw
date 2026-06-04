package com.company.agw.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    private String retCode;
    private String retMsg;
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .retCode(ResponseCode.SUCCESS.getRetCode())
                .retMsg(ResponseCode.SUCCESS.getRetMsg())
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> fail(ResponseCode responseCode) {
        return fail(responseCode.getRetCode(), responseCode.getRetMsg());
    }

    public static <T> CommonResponse<T> fail(String retCode, String retMsg) {
        return CommonResponse.<T>builder()
                .retCode(retCode)
                .retMsg(retMsg)
                .data(null)
                .build();
    }
}
