package com.company.agw.domain.filter;

import com.company.agw.common.response.PassResponseCode;
import com.company.agw.domain.user.PassUserIdentity;
import com.company.agw.domain.user.PassUserIdentityResolver;
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

    private final PassUserIdentityResolver passUserIdentityResolver;
    private final FilterMapper filterMapper;

    @Transactional(readOnly = true)
    public GetUserFilterWhiteResponse getUserFilterWhite(GetUserFilterWhiteRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return GetUserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return GetUserFilterWhiteResponse.notJoined();
            }

            Map<String, List<List<Object>>> filters = filterMapper.selectWhiteFiltersByPass(identity.custNum())
                    .stream()
                    .collect(Collectors.groupingBy(
                            PassFilterRowEntity::getFilterGroup,
                            Collectors.mapping(this::toPassFilterRow, Collectors.toList())
                    ));

            return GetUserFilterWhiteResponse.success(
                    userID,
                    filters.getOrDefault(PassFilterCommandKind.WHITE_NUMBER.passFieldName(), List.of()),
                    filters.getOrDefault(PassFilterCommandKind.WHITE_PATTERN.passFieldName(), List.of()),
                    filters.getOrDefault(PassFilterCommandKind.WHITE_ADDRESS.passFieldName(), List.of())
            );
        } catch (Exception e) {
            return GetUserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    @Transactional(readOnly = true)
    public GetUserFilterBlackResponse getUserFilterBlack(GetUserFilterBlackRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return GetUserFilterBlackResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return GetUserFilterBlackResponse.notJoined();
            }

            Map<String, List<List<Object>>> filters = filterMapper.selectBlackFiltersByPass(identity.custNum())
                    .stream()
                    .collect(Collectors.groupingBy(
                            PassFilterRowEntity::getFilterGroup,
                            Collectors.mapping(this::toPassFilterRow, Collectors.toList())
                    ));

            return GetUserFilterBlackResponse.success(
                    userID,
                    filters.getOrDefault("blackNUM", List.of()),
                    filters.getOrDefault("blackPattern", List.of()),
                    filters.getOrDefault("blackPrefix", List.of()),
                    filters.getOrDefault("prefixPool", List.of())
            );
        } catch (Exception e) {
            return GetUserFilterBlackResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    @Transactional
    public SetUserFilterWhiteResponse setUserFilterWhite(SetUserFilterWhiteRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return SetUserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isValidSetUserFilterWhiteRequest(request)) {
            return SetUserFilterWhiteResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return SetUserFilterWhiteResponse.notJoined();
            }

            return SetUserFilterWhiteResponse.success(
                    userID,
                    processFilterRows(identity.custNum(), request.getWhiteNUM(), PassFilterCommandKind.WHITE_NUMBER),
                    processFilterRows(identity.custNum(), request.getWhitePattern(), PassFilterCommandKind.WHITE_PATTERN),
                    processFilterRows(identity.custNum(), request.getWhiteNUMAddr(), PassFilterCommandKind.WHITE_ADDRESS)
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

    @Transactional
    public SetUserFilterBlackResponse setUserFilterBlack(SetUserFilterBlackRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return SetUserFilterBlackResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isValidSetUserFilterBlackRequest(request)) {
            return SetUserFilterBlackResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return SetUserFilterBlackResponse.notJoined();
            }

            return SetUserFilterBlackResponse.success(
                    userID,
                    processFilterRows(identity.custNum(), request.getBlackNUM(), PassFilterCommandKind.BLACK_NUMBER),
                    processFilterRows(identity.custNum(), request.getBlackPattern(), PassFilterCommandKind.BLACK_PATTERN),
                    processFilterRows(identity.custNum(), request.getBlackPrefix(), PassFilterCommandKind.BLACK_PREFIX)
            );
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return SetUserFilterBlackResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    private List<List<Object>> processFilterRows(
            String decodeUserID,
            List<List<Object>> requestRows,
            PassFilterCommandKind filterKind
    ) {
        if (requestRows.isEmpty()) {
            return List.of();
        }

        return requestRows.stream()
                .map(requestRow -> processFilterRow(decodeUserID, requestRow, filterKind))
                .toList();
    }

    private List<Object> processFilterRow(String decodeUserID, List<Object> requestRow, PassFilterCommandKind filterKind) {
        PassFilterCommand command = PassFilterCommand.fromPassRow(decodeUserID, requestRow);
        String saveDt = nowPassDate();

        if (!command.isValidFor(filterKind)) {
            return PassFilterCommandResult.of(
                    command,
                    PassFilterCommandResult.INVALID_REQUEST,
                    command.id(),
                    ""
            ).toPassRow();
        }

        if (command.needsStoredData() && hasWhiteDuplicate(command, filterKind)) {
            return PassFilterCommandResult.of(
                    command,
                    PassFilterCommandResult.DUPLICATED_WHITE,
                    command.id(),
                    ""
            ).toPassRow();
        }

        if (command.needsStoredData() && hasBlackDuplicate(command, filterKind)) {
            return PassFilterCommandResult.of(
                    command,
                    PassFilterCommandResult.DUPLICATED_BLACK,
                    command.id(),
                    ""
            ).toPassRow();
        }

        PassFilterCommandEntity entity = command.toEntity(saveDt);
        int affectedRows = executeFilterCommand(entity, command.cmdType(), filterKind);
        int result = affectedRows > 0 ? PassFilterCommandResult.SUCCESS : PassFilterCommandResult.FAILED;

        return PassFilterCommandResult.of(command, result, entity.getId(), saveDt).toPassRow();
    }

    private boolean hasWhiteDuplicate(PassFilterCommand command, PassFilterCommandKind filterKind) {
        String excludeId = command.isUpdate() ? command.id() : null;
        return switch (filterKind) {
            case WHITE_NUMBER, BLACK_NUMBER -> filterMapper.countWhiteNumberByData(command.custNum(), command.data(), excludeId) > 0;
            case WHITE_PATTERN, BLACK_PATTERN -> filterMapper.countWhitePatternByData(command.custNum(), command.data(), excludeId) > 0;
            case WHITE_ADDRESS -> filterMapper.countWhiteAddressByData(command.custNum(), command.data(), excludeId) > 0;
            case BLACK_PREFIX -> false;
        };
    }

    private boolean hasBlackDuplicate(PassFilterCommand command, PassFilterCommandKind filterKind) {
        String excludeId = command.isUpdate() ? command.id() : null;
        return switch (filterKind) {
            case WHITE_NUMBER, BLACK_NUMBER -> filterMapper.countBlackNumberByData(command.custNum(), command.data(), excludeId) > 0;
            case WHITE_PATTERN, BLACK_PATTERN -> filterMapper.countBlackPatternByData(command.custNum(), command.data(), excludeId) > 0;
            case WHITE_ADDRESS -> false;
            case BLACK_PREFIX -> filterMapper.countBlackPrefixByData(command.custNum(), command.data(), excludeId) > 0;
        };
    }

    private int executeFilterCommand(PassFilterCommandEntity entity, Integer cmdType, PassFilterCommandKind filterKind) {
        return switch (filterKind) {
            case WHITE_NUMBER -> executeWhiteNumberCommand(entity, cmdType);
            case WHITE_PATTERN -> executeWhitePatternCommand(entity, cmdType);
            case WHITE_ADDRESS -> executeWhiteAddressCommand(entity, cmdType);
            case BLACK_NUMBER -> executeBlackNumberCommand(entity, cmdType);
            case BLACK_PATTERN -> executeBlackPatternCommand(entity, cmdType);
            case BLACK_PREFIX -> executeBlackPrefixCommand(entity, cmdType);
        };
    }

    private int executeWhiteNumberCommand(PassFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case PassFilterCommand.CMD_CREATE -> filterMapper.insertWhiteNumber(entity);
            case PassFilterCommand.CMD_DELETE -> filterMapper.deleteWhiteNumber(entity.getCustNum(), entity.getId());
            case PassFilterCommand.CMD_UPDATE -> filterMapper.updateWhiteNumber(entity);
            default -> 0;
        };
    }

    private int executeWhitePatternCommand(PassFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case PassFilterCommand.CMD_CREATE -> filterMapper.insertWhitePattern(entity);
            case PassFilterCommand.CMD_DELETE -> filterMapper.deleteWhitePattern(entity.getCustNum(), entity.getId());
            case PassFilterCommand.CMD_UPDATE -> filterMapper.updateWhitePattern(entity);
            default -> 0;
        };
    }

    private int executeWhiteAddressCommand(PassFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case PassFilterCommand.CMD_CREATE -> filterMapper.insertWhiteAddress(entity);
            case PassFilterCommand.CMD_DELETE -> filterMapper.deleteWhiteAddress(entity.getCustNum(), entity.getId());
            case PassFilterCommand.CMD_UPDATE -> filterMapper.updateWhiteAddress(entity);
            default -> 0;
        };
    }

    private int executeBlackNumberCommand(PassFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case PassFilterCommand.CMD_CREATE -> filterMapper.insertBlackNumber(entity);
            case PassFilterCommand.CMD_DELETE -> filterMapper.deleteBlackNumber(entity.getCustNum(), entity.getId());
            case PassFilterCommand.CMD_UPDATE -> filterMapper.updateBlackNumber(entity);
            default -> 0;
        };
    }

    private int executeBlackPatternCommand(PassFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case PassFilterCommand.CMD_CREATE -> filterMapper.insertBlackPattern(entity);
            case PassFilterCommand.CMD_DELETE -> filterMapper.deleteBlackPattern(entity.getCustNum(), entity.getId());
            case PassFilterCommand.CMD_UPDATE -> filterMapper.updateBlackPattern(entity);
            default -> 0;
        };
    }

    private int executeBlackPrefixCommand(PassFilterCommandEntity entity, Integer cmdType) {
        return switch (cmdType) {
            case PassFilterCommand.CMD_CREATE -> filterMapper.insertBlackPrefix(entity);
            case PassFilterCommand.CMD_DELETE -> filterMapper.deleteBlackPrefix(entity.getCustNum(), entity.getId());
            case PassFilterCommand.CMD_UPDATE -> filterMapper.updateBlackPrefix(entity);
            default -> 0;
        };
    }

    private List<Object> toPassFilterRow(PassFilterRowEntity entity) {
        return PassFilterResponseRow.fromEntity(entity).toPassRow();
    }

    private boolean isValidSetUserFilterWhiteRequest(SetUserFilterWhiteRequest request) {
        return request != null
                && request.getWhiteNUM() != null
                && request.getWhitePattern() != null
                && request.getWhiteNUMAddr() != null;
    }

    private boolean isValidSetUserFilterBlackRequest(SetUserFilterBlackRequest request) {
        return request != null
                && request.getBlackNUM() != null
                && request.getBlackPattern() != null
                && request.getBlackPrefix() != null;
    }

    private String nowPassDate() {
        return LocalDateTime.now().format(PASS_DATE_FORMATTER);
    }
}
