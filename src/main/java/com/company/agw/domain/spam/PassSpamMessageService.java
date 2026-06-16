package com.company.agw.domain.spam;

import com.company.agw.auth.AuthService;
import com.company.agw.common.response.PassResponseCode;
import com.company.agw.domain.user.PassUserIdentity;
import com.company.agw.domain.user.PassUserIdentityResolver;
import com.company.agw.external.rcs.RcsClient;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassSpamMessageService {

    private static final int PASS_MAX_SIZE = 200;
    private static final int IMAGE_RETENTION_DAYS = 7;
    private static final DateTimeFormatter PASS_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final AuthService authService;
    private final PassUserIdentityResolver passUserIdentityResolver;
    private final PassSpamMessageMapper passSpamMessageMapper;
    private final WebClient.Builder webClientBuilder;
    private final RcsClient rcsClient;

    @Value("${external.rcs.new-spam-onoff:1}")
    private String newRcsSpamOnoff;

    @Transactional(readOnly = true)
    public GetSpamMsgListResponse getSpamMsgList(GetSpamMsgListRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return GetSpamMsgListResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return GetSpamMsgListResponse.notJoined();
            }

            List<List<Object>> spamMsgList = passSpamMessageMapper.selectSpamMessagesByPass(identity.custNum(), PASS_MAX_SIZE)
                    .stream()
                    .map(this::toPassSpamMessageRow)
                    .toList();

            return GetSpamMsgListResponse.success(userID, spamMsgList);
        } catch (Exception e) {
            return GetSpamMsgListResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    @Transactional(readOnly = true)
    public GetSpamFileDataResponse getSpamFileData(GetSpamFileDataRequest request) {
        String userID = request == null ? null : request.getUserID();
        String seqNO = request == null ? null : request.getSeqNO();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return GetSpamFileDataResponse.fail(
                    userID,
                    seqNO,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isValidGetSpamFileDataRequest(request)) {
            return GetSpamFileDataResponse.fail(
                    userID,
                    seqNO,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return GetSpamFileDataResponse.notJoined(seqNO);
            }

            String fileName = request.getFileName().trim();
            if (!"8".equals(request.getMsgType())) {
                PassSpamFileEntity spamFile = passSpamMessageMapper.selectSpamFileByPass(identity.custNum(), seqNO);
                if (spamFile == null) {
                    return GetSpamFileDataResponse.fail(
                            userID,
                            seqNO,
                            PassResponseCode.RESTORE_MESSAGE_NOT_FOUND.getRetCode(),
                            PassResponseCode.RESTORE_MESSAGE_NOT_FOUND.getRetMsg()
                    );
                }
                if (isExpired(spamFile.getSaveDt(), IMAGE_RETENTION_DAYS)) {
                    return GetSpamFileDataResponse.fail(
                            userID,
                            seqNO,
                            PassResponseCode.IMAGE_EXPIRED.getRetCode(),
                            PassResponseCode.IMAGE_EXPIRED.getRetMsg()
                    );
                }
                fileName = selectRequestedFileName(fileName, spamFile.getImageFileName());
            }

            String fileData = readFileDataAsBase64(fileName);
            return GetSpamFileDataResponse.success(userID, seqNO, List.of(Map.of(fileName, fileData)));
        } catch (Exception e) {
            return GetSpamFileDataResponse.fail(
                    userID,
                    seqNO,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    @Transactional(readOnly = true)
    public GetDownloadFileResponse getDownloadFile(GetDownloadFileRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return GetDownloadFileResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isValidGetDownloadFileRequest(request)) {
            return GetDownloadFileResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isNewRcsDownloadEnabled()) {
            return GetDownloadFileResponse.fail(
                    userID,
                    PassResponseCode.IMAGE_EXPIRED.getRetCode(),
                    PassResponseCode.IMAGE_EXPIRED.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return GetDownloadFileResponse.notJoined();
            }

            String encryptedFileName = request.getFileName().trim();
            String downloadUrl = authService.decryptPassFileName(encryptedFileName, identity.custNum());
            if (!isHttpUrl(downloadUrl)) {
                log.warn(
                        "getDownloadFile decrypted fileName is not http url. custNum={}, msgType={}, fileNameLength={}, decryptedValue={}",
                        identity.custNum(),
                        request.getMsgType(),
                        encryptedFileName.length(),
                        downloadUrl
                );
                return GetDownloadFileResponse.fail(
                        userID,
                        PassResponseCode.INVALID_PARAMETER.getRetCode(),
                        PassResponseCode.INVALID_PARAMETER.getRetMsg()
                );
            }

            String fileData = readFileDataAsBase64(downloadUrl);
            return GetDownloadFileResponse.success(userID, List.of(Map.of(encryptedFileName, fileData)));
        } catch (Exception e) {
            log.warn(
                    "getDownloadFile failed. custNum={}, msgType={}, fileNameLength={}",
                    identity.custNum(),
                    request == null ? null : request.getMsgType(),
                    request == null || request.getFileName() == null ? 0 : request.getFileName().trim().length(),
                    e
            );
            return GetDownloadFileResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }


    @Transactional
    public RemoveSpamMsgResponse removeSpamMsg(RemoveSpamMsgRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return RemoveSpamMsgResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isValidRemoveSpamMsgRequest(request)) {
            return RemoveSpamMsgResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return RemoveSpamMsgResponse.notJoined();
            }

            if ("8".equals(request.getMsgType())) {
                String telId = resolveRcsMessageId(identity.custNum(), request.getSeqNO());
                if (!hasText(telId)) {
                    return RemoveSpamMsgResponse.fail(
                            userID,
                            PassResponseCode.DELETE_MESSAGE_NOT_FOUND.getRetCode(),
                            PassResponseCode.DELETE_MESSAGE_NOT_FOUND.getRetMsg()
                    );
                }

                return RemoveSpamMsgResponse.fail(
                        userID,
                        PassResponseCode.UNSUPPORTED_FEATURE.getRetCode(),
                        PassResponseCode.UNSUPPORTED_FEATURE.getRetMsg()
                );
            }

            int affectedRows = "all".equalsIgnoreCase(request.getSeqNO())
                    ? passSpamMessageMapper.deleteSpamMessagesByCustNum(identity.custNum())
                    : passSpamMessageMapper.deleteSpamMessageBySeqNo(request.getSeqNO());

            if (affectedRows < 1 && !"all".equalsIgnoreCase(request.getSeqNO())) {
                return RemoveSpamMsgResponse.fail(
                        userID,
                        PassResponseCode.DELETE_MESSAGE_NOT_FOUND.getRetCode(),
                        PassResponseCode.DELETE_MESSAGE_NOT_FOUND.getRetMsg()
                );
            }

            RemoveSpamMsgResponse response = RemoveSpamMsgResponse.success(userID);
            passSpamMessageMapper.insertRemoveSpamMsgHistory(
                    identity.custNum(),
                    String.valueOf(response.getRetCode()),
                    response.getRetMsg()
            );
            return response;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RemoveSpamMsgResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    @Transactional
    public RecoverySpamMsgResponse recoverySpamMsg(RecoverySpamMsgRequest request) {
        String userID = request == null ? null : request.getUserID();
        String seqNO = request == null ? null : request.getSeqNO();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return RecoverySpamMsgResponse.fail(
                    userID,
                    seqNO,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isValidRecoverySpamMsgRequest(request)) {
            return RecoverySpamMsgResponse.fail(
                    userID,
                    seqNO,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return RecoverySpamMsgResponse.notJoined(seqNO);
            }

            if ("8".equals(request.getMsgType())) {
                return recoverNewRcsSpamMsg(userID, identity.custNum(), seqNO);
            }

            PassSpamMessageEntity spamMessage = passSpamMessageMapper.selectRecoverySpamSMS(
                    identity.custNum(),
                    request.getMsgType(),
                    seqNO
            );
            if (spamMessage == null) {
                return RecoverySpamMsgResponse.fail(
                        userID,
                        seqNO,
                        PassResponseCode.RESTORE_MESSAGE_NOT_FOUND.getRetCode(),
                        PassResponseCode.RESTORE_MESSAGE_NOT_FOUND.getRetMsg()
                );
            }

            passSpamMessageMapper.insertMfsRecoverMessage(
                    spamMessage.getSmsSeq(),
                    toRecoverMessageType(spamMessage.getSmsKind()),
                    spamMessage.getCustNum()
            );

            RecoverySpamMsgResponse response = RecoverySpamMsgResponse.success(userID, seqNO);
            insertRecoveryHistory(spamMessage.getCustNum(), "1", response.getRetMsg());
            return response;
        } catch (Exception e) {
            return RecoverySpamMsgResponse.fail(
                    userID,
                    seqNO,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    private RecoverySpamMsgResponse recoverNewRcsSpamMsg(String userID, String decodeUserID, String seqNO) {
        PassSpamMessageEntity rcsMessage = passSpamMessageMapper.getTelIdFromSmsSeq(decodeUserID, seqNO);
        if (rcsMessage == null || !hasText(rcsMessage.getTelId())) {
            return RecoverySpamMsgResponse.fail(
                    userID,
                    seqNO,
                    PassResponseCode.RESTORE_MESSAGE_NOT_FOUND.getRetCode(),
                    PassResponseCode.RESTORE_MESSAGE_NOT_FOUND.getRetMsg()
            );
        }

        try {
            rcsClient.restoreMessage(
                    rcsMessage.getTelId(),
                    rcsMessage.getSrcNum(),
                    rcsMessage.getCustNum(),
                    rcsMessage.getMsgOrigination()
            );
        } catch (Exception e) {
            RecoverySpamMsgResponse response = RecoverySpamMsgResponse.fail(
                    userID,
                    seqNO,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
            insertRecoveryHistory(rcsMessage.getCustNum(), "2", response.getRetMsg());
            return response;
        }

        RecoverySpamMsgResponse response = RecoverySpamMsgResponse.success(userID, seqNO);
        insertRecoveryHistory(rcsMessage.getCustNum(), "1", response.getRetMsg());
        return response;
    }

    private List<Object> toPassSpamMessageRow(PassSpamMessageEntity entity) {
        String smsClc = defaultText(entity.getSmsClc());
        List<Object> row = new ArrayList<>();
        row.add(defaultText(entity.getSmsSeq()));
        row.add(defaultText(entity.getSmsKind()));
        row.add(defaultText(entity.getCbNum()));
        row.add(toPassDate(smsClc));
        row.add(toPassTime(smsClc));
        row.add(defaultText(entity.getCbUrl()));
        row.add(defaultText(entity.getFilterKind()));
        row.add(toPassBlockReason(entity.getFilterKind(), entity.getSpamWord()));
        row.add(defaultText(entity.getSmsMsg()));
        row.add(defaultText(entity.getImageFileName()));
        return row;
    }

    private String toPassDate(String smsClc) {
        if (smsClc.length() < 8) {
            return "";
        }

        return smsClc.substring(0, 8);
    }

    private String toPassTime(String smsClc) {
        if (smsClc.length() < 14) {
            return smsClc.length() >= 8 ? smsClc.substring(8) : "";
        }

        return smsClc.substring(8, 14);
    }

    private String toPassBlockReason(String filterKind, String spamWord) {
        if ("P".equals(filterKind)) {
            return "경찰청 차단";
        }

        if ("b".equals(filterKind) || "c".equals(filterKind) || "d".equals(filterKind)) {
            return defaultText(spamWord);
        }

        return "운영자 지능형 스팸 차단";
    }

    private String selectRequestedFileName(String requestFileName, String savedFileNames) {
        if (!hasText(savedFileNames)) {
            return requestFileName;
        }

        for (String savedFileName : savedFileNames.split("[;,]")) {
            String trimmedSavedFileName = savedFileName.trim();
            if (trimmedSavedFileName.equals(requestFileName)
                    || fileNameOnly(trimmedSavedFileName).equals(requestFileName)) {
                return trimmedSavedFileName;
            }
        }

        return requestFileName;
    }

    private String fileNameOnly(String value) {
        try {
            if (value.startsWith("http://") || value.startsWith("https://")) {
                String path = URI.create(value).getPath();
                int index = path.lastIndexOf('/');
                return index < 0 ? path : path.substring(index + 1);
            }

            return Path.of(value).getFileName().toString();
        } catch (Exception e) {
            return value;
        }
    }

    private String readFileDataAsBase64(String fileName) {
        byte[] fileBytes;
        if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
            fileBytes = webClientBuilder.build()
                    .get()
                    .uri(URI.create(fileName))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } else {
            try {
                fileBytes = Files.readAllBytes(Path.of(fileName));
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to read file data", e);
            }
        }

        if (fileBytes == null) {
            throw new IllegalArgumentException("File data is empty");
        }

        return Base64.getEncoder().encodeToString(fileBytes);
    }

    private boolean isValidGetSpamFileDataRequest(GetSpamFileDataRequest request) {
        return request != null
                && hasText(request.getMsgType())
                && hasText(request.getSeqNO())
                && hasText(request.getFileName())
                && isValidMsgType(request.getMsgType())
                && byteLength(request.getSeqNO()) <= 40;
    }

    private boolean isValidGetDownloadFileRequest(GetDownloadFileRequest request) {
        return request != null
                && hasText(request.getMsgType())
                && hasText(request.getFileName())
                && ("5".equals(request.getMsgType()) || "8".equals(request.getMsgType()));
    }

    private boolean isValidRemoveSpamMsgRequest(RemoveSpamMsgRequest request) {
        return request != null
                && hasText(request.getMsgType())
                && hasText(request.getSeqNO())
                && isValidMsgType(request.getMsgType())
                && ("all".equalsIgnoreCase(request.getSeqNO()) || byteLength(request.getSeqNO()) <= 40);
    }

    private boolean isValidRecoverySpamMsgRequest(RecoverySpamMsgRequest request) {
        return request != null
                && hasText(request.getMsgType())
                && hasText(request.getSeqNO())
                && isValidMsgType(request.getMsgType())
                && byteLength(request.getSeqNO()) <= 40;
    }

    private String toRecoverMessageType(String smsKind) {
        return hasText(smsKind) ? smsKind : "";
    }

    private void insertRecoveryHistory(String custNum, String rst, String jobMsg) {
        if (hasText(custNum)) {
            try {
                passSpamMessageMapper.insertRecoverySpamMsgHistory(custNum, rst, jobMsg);
            } catch (Exception ignored) {
                // History failure must not hide the restore result returned to PASS.
            }
        }
    }

    private String resolveRcsMessageId(String decodeUserID, String seqNO) {
        if (!"all".equalsIgnoreCase(seqNO)) {
            String telId = passSpamMessageMapper.selectTelIdByPass(decodeUserID, seqNO);
            return hasText(telId) ? telId : seqNO;
        }

        return seqNO;
    }

    private boolean isValidMsgType(String msgType) {
        return "1".equals(msgType)
                || "3".equals(msgType)
                || "4".equals(msgType)
                || "5".equals(msgType)
                || "8".equals(msgType);
    }

    private boolean isNewRcsDownloadEnabled() {
        return !"0".equals(newRcsSpamOnoff);
    }

    private boolean isHttpUrl(String value) {
        return value != null && (value.startsWith("http://") || value.startsWith("https://"));
    }

    private boolean isExpired(String saveDt, int retentionDays) {
        if (!hasText(saveDt)) {
            return false;
        }

        try {
            LocalDateTime savedAt = LocalDateTime.parse(normalizeDateTime(saveDt), PASS_DATE_TIME_FORMATTER);
            return savedAt.plusDays(retentionDays).isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }

    private String normalizeDateTime(String value) {
        String digits = value.chars()
                .filter(Character::isDigit)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        if (digits.length() >= 14) {
            return digits.substring(0, 14);
        }

        return String.format("%-14s", digits).replace(' ', '0');
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
