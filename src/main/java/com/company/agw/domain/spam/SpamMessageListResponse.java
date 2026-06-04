package com.company.agw.domain.spam;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpamMessageListResponse {

    private List<SpamMessageResponse> messages;
}
