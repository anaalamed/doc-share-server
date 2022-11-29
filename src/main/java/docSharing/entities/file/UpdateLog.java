package docSharing.entities.file;

import docSharing.controller.request.UpdateRequest;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Entity
public class UpdateLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "update_id")
    private UpdateRequest updateRequest;
    private LocalDateTime timestamp;

    public UpdateLog(UpdateRequest updateRequest, LocalDateTime timestamp) {
        this.updateRequest = updateRequest;
        this.timestamp = timestamp;
    }

    public UpdateRequest getUpdateRequest() {
        return updateRequest;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setUpdateRequest(UpdateRequest updateRequest) {
        this.updateRequest = updateRequest;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isContinuousLog(UpdateLog updateLog) {
        return (isSameUser(updateLog) &&
                isMatchingType(updateLog) &&
                isFromLastXSeconds(updateLog, 5) &&
                isContinuousIndex(updateLog));
    }

    public void unite(UpdateLog updateLog) {
        int previousStart       = this.getUpdateRequest().getStartPosition();
        int previousEnd         = this.getUpdateRequest().getEndPosition();
        int currentStart        = updateLog.getUpdateRequest().getStartPosition();
        int currentEnd          = updateLog.getUpdateRequest().getEndPosition();

        switch(this.getUpdateRequest().getType()) {
            case APPEND:
            case APPEND_RANGE:
                appendContent(updateLog);
                this.updateRequest.setStartPosition(min(previousStart, currentStart));
                this.updateRequest.setEndPosition(max(previousEnd, currentEnd));
                break;

            case DELETE:
                this.updateRequest.setEndPosition(min(previousEnd, currentEnd));
                break;

            case DELETE_RANGE:
                this.updateRequest.setStartPosition(min(previousStart, currentEnd));
                break;

            default:
                throw new IllegalArgumentException(
                        String.format("Update type: %s is not supported!", updateLog.getUpdateRequest().getType()));
        }

        this.setTimestamp(updateLog.getTimestamp());
    }

    private void appendContent(UpdateLog updateLog) {
        String previousContent  = this.updateRequest.getContent();
        int previousStart       = this.getUpdateRequest().getStartPosition();
        int currentStart        = updateLog.getUpdateRequest().getStartPosition();
        int currentEnd          = updateLog.getUpdateRequest().getEndPosition();

        this.updateRequest.setContent(previousContent.substring(0, currentStart - previousStart)
                + updateLog.getUpdateRequest().getContent()
                + previousContent.substring(min(previousContent.length(), currentEnd - previousStart)));
    }

    private boolean isSameUser(UpdateLog updateLog) {
        return this.getUpdateRequest().getUserEmail() == updateLog.getUpdateRequest().getUserEmail();
    }

    private boolean isMatchingType(UpdateLog updateLog) {
        switch (this.getUpdateRequest().getType()) {
            case APPEND:
            case APPEND_RANGE:
                return updateLog.getUpdateRequest().getType() == UpdateRequest.UpdateType.APPEND;

            case DELETE:
            case DELETE_RANGE:
                return updateLog.getUpdateRequest().getType() == UpdateRequest.UpdateType.DELETE;

            default:
                throw new IllegalArgumentException(
                        String.format("Update type: %s is not supported!", this.getUpdateRequest().getType()));
        }
    }

    private boolean isFromLastXSeconds(UpdateLog updateLog, int seconds) {
        return this.getTimestamp().isAfter(updateLog.getTimestamp().minusSeconds(seconds));
    }

    private boolean isContinuousIndex(UpdateLog updateLog) {
        int previousStart = this.getUpdateRequest().getStartPosition();
        int previousEnd = this.getUpdateRequest().getEndPosition();
        int currentStart = updateLog.getUpdateRequest().getStartPosition();

        switch (this.getUpdateRequest().getType()) {
            case APPEND:
            case DELETE:
                return currentStart >= previousStart && currentStart <= previousEnd + 1;

            case APPEND_RANGE:
            case DELETE_RANGE:
                return currentStart >= previousStart &&
                        currentStart <= previousStart + this.getUpdateRequest().getContent().length();
            default:
                throw new IllegalArgumentException(
                        String.format("Update type: %s is not supported!", updateLog.getUpdateRequest().getType()));
        }
    }

    @Override
    public String toString() {
        return "UpdateLog{" +
                "updateRequest=" + updateRequest +
                ", timestamp=" + timestamp +
                '}';
    }
}
