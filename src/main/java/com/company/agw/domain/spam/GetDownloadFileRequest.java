package com.company.agw.domain.spam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDownloadFileRequest {

    @JsonProperty("userID")
    private String userID;

    private String msgType;
    private String fileName;
}
