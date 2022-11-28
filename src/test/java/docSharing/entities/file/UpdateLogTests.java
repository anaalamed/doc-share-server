package docSharing.entities.file;

import docSharing.controller.request.UpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(false, this.updateLog.isContinuousLog(arbitraryLog),
                String.format("isContinuousLog() should return false for different users"));
    }

    @Test
    @DisplayName("isContinuousLog() returns false when type is different")
    void isContinuousLog_DifferentType_ReturnsFalse() {
        UpdateRequest.UpdateType type1 = UpdateRequest.UpdateType.APPEND;
        UpdateRequest.UpdateType type2 = UpdateRequest.UpdateType.DELETE;

        UpdateLog arbitraryLog = createArbitraryUpdateLog();
        this.updateLog.getUpdateRequest().setType(type1);
        arbitraryLog.getUpdateRequest().setType(type2);

        assertEquals(false, this.updateLog.isContinuousLog(arbitraryLog),
                String.format("isContinuousLog() should return false for different types"));
    }

    @Test
    @DisplayName("isContinuousLog() returns false when more than 5 seconds passed")
    void isContinuousLog_DistantTimeStamp_ReturnsFalse() {
        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = timestamp1.plusSeconds(5);

        UpdateLog arbitraryLog = createArbitraryUpdateLog();
        this.updateLog.setTimestamp(timestamp1);
        arbitraryLog.setTimestamp(timestamp2);

        assertEquals(false, this.updateLog.isContinuousLog(arbitraryLog),
                String.format("isContinuousLog() should return false for distant timestamps"));
    }

    @Test
    @DisplayName("isContinuousLog() returns false when indexes are not continuous")
    void isContinuousLog_NonContinuousIndexes_ReturnsFalse() {
        UpdateLog arbitraryLog = createArbitraryUpdateLog();
        this.updateLog.getUpdateRequest().setStartPosition(20);
        this.updateLog.getUpdateRequest().setEndPosition(22);
        arbitraryLog.getUpdateRequest().setStartPosition(24);
        arbitraryLog.getUpdateRequest().setEndPosition(25);

        assertEquals(false, this.updateLog.isContinuousLog(arbitraryLog),
                String.format("isContinuousLog() should return false when indexes are not continuous"));
    }

    UpdateLog createArbitraryUpdateLog() {
        UpdateRequest updateRequest = new UpdateRequest.UpdateRequestBuilder()
                .setUserEmail("lior.mathan@gmail.com").setType(UpdateRequest.UpdateType.APPEND)
                .setStartPosition(10).setEndPosition(12).build();
        return new UpdateLog(updateRequest, LocalDateTime.now());
    }
}
