package com.company.agw.domain.keyword;

import com.company.agw.common.response.PassResponseCode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassKeywordService {

    private static final String PASS_CHANNEL = "PASS";

    private final PassKeywordMapper passKeywordMapper;

    @Value("${pass.keyword.save-dt:20250213}")
    private String keywordSaveDt;

    @Transactional(readOnly = true)
    public GetKeywordInfoResponse getKeywordInfo(GetKeywordInfoRequest request) {
        String themeCode = request == null ? null : request.getThemeCode();
        if (hasText(themeCode) && byteLength(themeCode) > 10) {
            return GetKeywordInfoResponse.fail(
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            List<List<Object>> keywordInfoList = passKeywordMapper
                    .selectPassKeywords(PASS_CHANNEL, keywordSaveDt, normalizeThemeCode(themeCode))
                    .stream()
                    .map(this::toKeywordInfoRow)
                    .toList();
            return GetKeywordInfoResponse.success(keywordInfoList);
        } catch (Exception e) {
            return GetKeywordInfoResponse.fail(
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    private List<Object> toKeywordInfoRow(PassKeywordEntity entity) {
        List<Object> row = new ArrayList<>();
        row.add(defaultText(entity.getSavedt()));
        row.add(defaultText(entity.getThemecode()));
        row.add(defaultText(entity.getDescription()));
        row.add(defaultText(entity.getSortkey()));
        row.add(defaultText(entity.getCategory()));
        row.add(defaultText(entity.getKeyword()));
        return row;
    }

    private String normalizeThemeCode(String themeCode) {
        return hasText(themeCode) ? themeCode.trim() : null;
    }

    private int byteLength(String value) {
        return value == null ? 0 : value.getBytes(StandardCharsets.UTF_8).length;
    }

    private String defaultText(String value) {
        return value == null ? "" : value;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
