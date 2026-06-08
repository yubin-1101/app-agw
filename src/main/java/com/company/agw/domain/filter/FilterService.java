package com.company.agw.domain.filter;

import com.company.agw.auth.AuthService;
import com.company.agw.common.response.PassResponseCode;
import com.company.agw.common.validation.RequestValidator;
import com.company.agw.domain.user.UserMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
public class FilterService {

    private static final DateTimeFormatter PASS_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final AuthService authService;
    private final RequestValidator requestValidator;
    private final FilterMapper filterMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public FilterListResponse getFilters(FilterListRequest request) {
        authService.authenticatePassRequest(request.getToken());
        requestValidator.requireText(request.getUserId(), "userId");

        List<FilterResponse> filters = filterMapper.selectFilters(request.getUserId(), request.getFilterType())
                .stream()
                .map(FilterResponse::from)
                .toList();

        return FilterListResponse.builder()
                .filters(filters)
                .build();
    }

    @Transactional(readOnly = true)
    public UserFilterWhiteResponse getUserFilterWhite(UserFilterWhiteRequest request) {
        String userID = request == null ? null : request.getUserID();
        String decodeUserID;

        try {
            decodeUserID = authService.decryptPassUserId(userID);
        } catch (Exception e) {
            return UserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!hasText(userID) || !isNumeric(decodeUserID)) {
            return UserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (userMapper.selectUserPrivateInfobyPass(decodeUserID) == null) {
                return UserFilterWhiteResponse.notJoined();
            }

            Map<String, List<List<Object>>> filters = filterMapper.selectWhiteFiltersByPass(decodeUserID)
                    .stream()
                    .collect(Collectors.groupingBy(
                            UserWhiteFilterEntity::getFilterGroup,
                            Collectors.mapping(this::toPassFilterRow, Collectors.toList())
                    ));

            return UserFilterWhiteResponse.success(
                    userID,
                    filters.getOrDefault(WhiteFilterKind.NUMBER.passFieldName(), List.of()),
                    filters.getOrDefault(WhiteFilterKind.PATTERN.passFieldName(), List.of()),
                    filters.getOrDefault(WhiteFilterKind.ADDRESS.passFieldName(), List.of())
            );
        } catch (Exception e) {
            return UserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    @Transactional
    public SetUserFilterWhiteResponse setUserFilterWhite(SetUserFilterWhiteRequest request) {
        String userID = request == null ? null : request.getUserID();
        String decodeUserID;

        try {
            decodeUserID = authService.decryptPassUserId(userID);
        } catch (Exception e) {
            return SetUserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!hasText(userID) || !isNumeric(decodeUserID) || !isValidSetUserFilterWhiteRequest(request)) {
            return SetUserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (userMapper.selectUserPrivateInfobyPass(decodeUserID) == null) {
                return SetUserFilterWhiteResponse.notJoined();
            }

            return SetUserFilterWhiteResponse.success(
                    userID,
                    processWhiteRows(decodeUserID, request.getWhiteNUM(), WhiteFilterKind.NUMBER),
                    processWhiteRows(decodeUserID, request.getWhitePattern(), WhiteFilterKind.PATTERN),
                    processWhiteRows(decodeUserID, request.getWhiteNUMAddr(), WhiteFilterKind.ADDRESS)
            );
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return SetUserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    private List<List<Object>> processWhiteRows(
            String decodeUserID,
            List<List<Object>> requestRows,
            WhiteFilterKind filterKind
    ) {
        if (requestRows.isEmpty()) {
            return List.of();
        }

        return requestRows.stream()
                .map(requestRow -> processWhiteRow(decodeUserID, requestRow, filterKind))
                .toList();
    }

    private List<Object> processWhiteRow(String decodeUserID, List<Object> requestRow, WhiteFilterKind filterKind) {
        WhiteFilterCommand command = WhiteFilterCommand.fromPassRow(decodeUserID, requestRow);
        String saveDt = nowPassDate();

        if (!command.isValidFor(filterKind)) {
            return WhiteFilterCommandResult.of(
                    command,
                    WhiteFilterCommandResult.INVALID_REQUEST,
                    command.id(),
                    ""
            ).toPassRow();
        }

        if (command.needsStoredData() && hasWhiteDuplicate(command, filterKind)) {
            return WhiteFilterCommandResult.of(
                    command,
                    WhiteFilterCommandResult.DUPLICATED_WHITE,
                    command.id(),
                    ""
            ).toPassRow();
        }

        if (command.needsStoredData() && hasBlackDuplicate(command, filterKind)) {
            return WhiteFilterCommandResult.of(
                    command,
                    WhiteFilterCommandResult.DUPLICATED_BLACK,
                    command.id(),
                    ""
            ).toPassRow();
        }

        UserWhiteFilterCommandEntity entity = command.toEntity(saveDt);
        int affectedRows = executeWhiteCommand(entity, command.cmdType(), filterKind);
        int result = affectedRows > 0 ? WhiteFilterCommandResult.SUCCESS : WhiteFilterCommandResult.FAILED;

        return WhiteFilterCommandResult.of(command, result, entity.getId(), saveDt).toPassRow();
    }

    private boolean hasWhiteDuplicate(WhiteFilterCommand command, WhiteFilterKind filterKind) {
        String excludeId = command.isUpdate() ? command.id() : null;
        return switch (filterKind) {
            case NUMBER -> filterMapper.countWhiteNumberByData(command.custNum(), command.data(), excludeId) > 0;
            case PATTERN -> filterMapper.countWhitePatternByData(command.custNum(), command.data(), excludeId) > 0;
            case ADDRESS -> filterMapper.countWhiteAddressByData(command.custNum(), command.data(), excludeId) > 0;
        };
    }

    private boolean hasBlackDuplicate(WhiteFilterCommand command, WhiteFilterKind filterKind) {
        // Blacklist table names and columns are required to implement result -3 accurately.
        return false;
    }

    private int executeWhiteCommand(UserWhiteFilterCommandEntity entity, Integer cmdType, WhiteFilterKind filterKind) {
        return switch (filterKind) {
            case NUMBER -> executeWhiteNumberCommand(entity, cmdType);
            case PATTERN -> executeWhitePatternCommand(entity, cmdType);
            case ADDRESS -> executeWhiteAddressCommand(entity, cmdType);
        };
    }

    private int executeWhiteNumberCommand(UserWhiteFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case WhiteFilterCommand.CMD_CREATE -> filterMapper.insertWhiteNumber(entity);
            case WhiteFilterCommand.CMD_DELETE -> filterMapper.deleteWhiteNumber(entity.getCustNum(), entity.getId());
            case WhiteFilterCommand.CMD_UPDATE -> filterMapper.updateWhiteNumber(entity);
            default -> 0;
        };
    }

    private int executeWhitePatternCommand(UserWhiteFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case WhiteFilterCommand.CMD_CREATE -> filterMapper.insertWhitePattern(entity);
            case WhiteFilterCommand.CMD_DELETE -> filterMapper.deleteWhitePattern(entity.getCustNum(), entity.getId());
            case WhiteFilterCommand.CMD_UPDATE -> filterMapper.updateWhitePattern(entity);
            default -> 0;
        };
    }

    private int executeWhiteAddressCommand(UserWhiteFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case WhiteFilterCommand.CMD_CREATE -> filterMapper.insertWhiteAddress(entity);
            case WhiteFilterCommand.CMD_DELETE -> filterMapper.deleteWhiteAddress(entity.getCustNum(), entity.getId());
            case WhiteFilterCommand.CMD_UPDATE -> filterMapper.updateWhiteAddress(entity);
            default -> 0;
        };
    }

    private List<Object> toPassFilterRow(UserWhiteFilterEntity entity) {
        return WhiteFilterResponseRow.fromEntity(entity).toPassRow();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isNumeric(String value) {
        return hasText(value) && value.chars().allMatch(Character::isDigit);
    }

    private boolean isValidSetUserFilterWhiteRequest(SetUserFilterWhiteRequest request) {
        return request != null
                && request.getWhiteNUM() != null
                && request.getWhitePattern() != null
                && request.getWhiteNUMAddr() != null;
    }

    private String nowPassDate() {
        return LocalDateTime.now().format(PASS_DATE_FORMATTER);
    }
}
