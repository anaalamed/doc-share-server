package docSharing.entities.file;

import docSharing.controller.request.UpdateRequest;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateLogTests {
    private UpdateLog updateLog;

    @BeforeEach
    void beforeEach() {
        this.updateLog = createArbitraryUpdateLog();
    }

    @Test
    @DisplayName("isContinuousLog() returns false when user is different")
    void isContinuousLog_DifferentUser_ReturnsFalse() {
        String userEmail1 = "lior.mathan@gmail.com";
        String userEmail2 = "tal@gmail.com";
        UpdateLog arbitraryLog = createArbitraryUpdateLog();
        this.updateLog.getUpdateRequest().setUserEmail(userEmail1);
        arbitraryLog.getUpdateRequest().setUserEmail(userEmail2);

        assertFalse(this.updateLog.isContinuousLog(arbitraryLog),
                "isContinuousLog() should return false for different users");
    }

    @Test
    @DisplayName("isContinuousLog() returns false when type is different")
    void isContinuousLog_DifferentType_ReturnsFalse() {
        UpdateRequest.UpdateType type1 = UpdateRequest.UpdateType.APPEND;
        UpdateRequest.UpdateType type2 = UpdateRequest.UpdateType.DELETE;

        UpdateLog arbitraryLog = createArbitraryUpdateLog();
        this.updateLog.getUpdateRequest().setType(type1);
        arbitraryLog.getUpdateRequest().setType(type2);

        assertFalse(this.updateLog.isContinuousLog(arbitraryLog),
                "isContinuousLog() should return false for different types");
    }

    @Test
    @DisplayName("isContinuousLog() returns false when more than 5 seconds passed")
    void isContinuousLog_DistantTimeStamp_ReturnsFalse() {
        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = timestamp1.plusSeconds(5);

        UpdateLog arbitraryLog = createArbitraryUpdateLog();
        this.updateLog.setTimestamp(timestamp1);
        arbitraryLog.setTimestamp(timestamp2);

        assertFalse(this.updateLog.isContinuousLog(arbitraryLog),
                "isContinuousLog() should return false for distant timestamps");
    }

    @Test
    @DisplayName("isContinuousLog() returns false when indexes are not continuous")
    void isContinuousLog_NonContinuousIndexes_ReturnsFalse() {
        UpdateLog arbitraryLog = createArbitraryUpdateLog();
        this.updateLog.getUpdateRequest().setStartPosition(20);
        this.updateLog.getUpdateRequest().setEndPosition(22);
        arbitraryLog.getUpdateRequest().setStartPosition(24);
        arbitraryLog.getUpdateRequest().setEndPosition(25);

        assertFalse(this.updateLog.isContinuousLog(arbitraryLog),
                "isContinuousLog() should return false when indexes are not continuous");
    }

    @Test
    @DisplayName("isContinuousLog() returns true when logs are continuous")
    void isContinuousLog_ContinuousLogs_ReturnsTrue() {
        UpdateLog arbitraryLog = createArbitraryUpdateLog();
        this.updateLog.getUpdateRequest().setStartPosition(20);
        this.updateLog.getUpdateRequest().setEndPosition(22);
        arbitraryLog.getUpdateRequest().setStartPosition(23);
        arbitraryLog.getUpdateRequest().setEndPosition(24);

        this.updateLog.getUpdateRequest().setType(UpdateRequest.UpdateType.APPEND);
        arbitraryLog.getUpdateRequest().setType(UpdateRequest.UpdateType.APPEND);

        String email = "lior.mathan@gmail.com";
        this.updateLog.getUpdateRequest().setUserEmail(email);
        arbitraryLog.getUpdateRequest().setUserEmail(email);

        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = timestamp1.plusSeconds(3);
        this.updateLog.setTimestamp(timestamp1);
        arbitraryLog.setTimestamp(timestamp2);

        assertTrue(this.updateLog.isContinuousLog(arbitraryLog),
                "isContinuousLog() should return true when logs are continuous");
    }

    @Test
    @DisplayName("unite() updates log's content for append UpdateLogs")
    void unite_AppendLogs_UpdatesContent() {
        Pair<String, String> contents = new Pair<>("Lior", "Mathan");
        Pair<UpdateLog, UpdateLog> continuousLogs = getContinuousLogs(UpdateRequest.UpdateType.APPEND, contents);
        continuousLogs.getKey().unite(continuousLogs.getValue());

        assertEquals("LiorMathan", continuousLogs.getKey().getUpdateRequest().getContent(),
                "isContinuousLog() should update content to be: LiorMathan");
    }

    @Test
    @DisplayName("unite() updates log's indexes for append UpdateLogs")
    void unite_AppendLogs_UpdatesIndexes() {
        Pair<String, String> contents = new Pair<>("Lior", "Mathan");
        Pair<UpdateLog, UpdateLog> continuousLogs = getContinuousLogs(UpdateRequest.UpdateType.APPEND, contents);
        continuousLogs.getKey().unite(continuousLogs.getValue());

        assertEquals(20, continuousLogs.getKey().getUpdateRequest().getStartPosition(),
                "isContinuousLog() should update startPosition to be 20");

        assertEquals(30, continuousLogs.getKey().getUpdateRequest().getEndPosition(),
                "isContinuousLog() should update EndPosition to be 30");
    }

    @Test
    @DisplayName("unite() updates log's indexes for delete UpdateLogs")
    void unite_DeleteLogs_UpdatesIndexes() {
        Pair<String, String> contents = new Pair<>("", "");
        Pair<UpdateLog, UpdateLog> continuousLogs = getContinuousLogs(UpdateRequest.UpdateType.DELETE, contents);
        continuousLogs.getKey().getUpdateRequest().setStartPosition(20);
        continuousLogs.getKey().getUpdateRequest().setEndPosition(18);
        continuousLogs.getValue().getUpdateRequest().setStartPosition(18);
        continuousLogs.getValue().getUpdateRequest().setEndPosition(17);

        continuousLogs.getKey().unite(continuousLogs.getValue());

        assertEquals(20, continuousLogs.getKey().getUpdateRequest().getStartPosition(),
                "isContinuousLog() should update startPosition to be 20");

        assertEquals(17, continuousLogs.getKey().getUpdateRequest().getEndPosition(),
                "isContinuousLog() should update EndPosition to be 17");
    }

    Pair<UpdateLog, UpdateLog> getContinuousLogs(UpdateRequest.UpdateType type, Pair<String, String> contents) {
        UpdateLog log1 = createArbitraryUpdateLog();
        UpdateLog log2 = createArbitraryUpdateLog();

        log1.getUpdateRequest().setStartPosition(20);
        log1.getUpdateRequest().setEndPosition(20 + contents.getKey().length());
        log2.getUpdateRequest().setStartPosition(20 + contents.getKey().length());
        log2.getUpdateRequest().setEndPosition(20 + contents.getKey().length() + contents.getValue().length());

        log1.getUpdateRequest().setType(type);
        log2.getUpdateRequest().setType(type);

        String email = "lior.mathan@gmail.com";
        log1.getUpdateRequest().setUserEmail(email);
        log2.getUpdateRequest().setUserEmail(email);

        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = timestamp1.plusSeconds(3);
        log1.setTimestamp(timestamp1);
        log2.setTimestamp(timestamp2);

        log1.getUpdateRequest().setContent(contents.getKey());
        log2.getUpdateRequest().setContent(contents.getValue());

        assertTrue(log1.isContinuousLog(log2));
        return new Pair<>(log1, log2);
    }

    UpdateLog createArbitraryUpdateLog() {
        UpdateRequest updateRequest = new UpdateRequest.UpdateRequestBuilder()
                .setUserEmail("lior.mathan@gmail.com").setType(UpdateRequest.UpdateType.APPEND)
                .setStartPosition(10).setEndPosition(12).build();
        return new UpdateLog(updateRequest, LocalDateTime.now());
    }
}
