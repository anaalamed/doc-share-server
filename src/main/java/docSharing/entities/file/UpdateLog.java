package docSharing.entities.file;

import docSharing.controller.request.UpdateRequest;

import javax.persistence.*;
import java.time.LocalDate;

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
    private LocalDate timestamp;

    public UpdateLog(UpdateRequest updateRequest, LocalDate timestamp) {
        this.updateRequest = updateRequest;
        this.timestamp = timestamp;
    }

    public UpdateLog(UpdateLogBuilder builder) {
        this.updateRequest = builder.updateRequest;
        this.timestamp = builder.timestamp;
    }

    public UpdateRequest getUpdateRequest() {
        return updateRequest;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setUpdateRequest(UpdateRequest updateRequest) {
        this.updateRequest = updateRequest;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UpdateLog{" +
                "updateRequest=" + updateRequest +
                ", timestamp=" + timestamp +
                '}';
    }

    public class UpdateLogBuilder {
        private UpdateRequest updateRequest;
        private LocalDate timestamp;

        public UpdateLogBuilder(UpdateRequest updateRequest, LocalDate timestamp) {
            this.updateRequest = updateRequest;
            this.timestamp = timestamp;
        }

        public UpdateLogBuilder append(UpdateLog updateLog) {
            String previousContent  = this.updateRequest.getContent();
            int previousStart       = this.updateRequest.getStartPosition();
            int previousEnd         = this.updateRequest.getEndPosition();
            int currentStart        = updateLog.getUpdateRequest().getStartPosition();
            int currentEnd          = updateLog.getUpdateRequest().getEndPosition();

            String updatedContent = previousContent.substring(0, currentStart - previousStart)
                    + updateLog.getUpdateRequest().getContent()
                    + previousContent.substring(currentEnd);

            this.updateRequest = new UpdateRequest.UpdateRequestBuilder().setContent(updatedContent)
                    .setType(UpdateRequest.UpdateType.APPEND)
                    .setUserEmail(updateLog.getUpdateRequest().getUserEmail())
                    .setStartPosition(min(previousStart, currentStart))
                    .setEndPosition(max(previousEnd, currentEnd)).build();

            this.timestamp = updateLog.getTimestamp();

            return this;
        }

        public UpdateLogBuilder appendRange(UpdateLog updateLog) {
            UpdateRequest deleteRangeRequest = new UpdateRequest.UpdateRequestBuilder()
                    .setStartPosition(updateLog.getUpdateRequest().getStartPosition())
                    .setEndPosition(updateLog.getUpdateRequest().getEndPosition()).build();

            UpdateRequest appendRequest = new UpdateRequest.UpdateRequestBuilder()
                    .setStartPosition(updateLog.getUpdateRequest().getStartPosition())
                    .setEndPosition(updateLog.getUpdateRequest().getEndPosition())
                    .setContent(updateLog.getUpdateRequest().getContent()).build();

            this.deleteRange(new UpdateLog(deleteRangeRequest, updateLog.getTimestamp()))
                    .append(new UpdateLog(appendRequest, updateLog.getTimestamp()));

            this.updateRequest.setType(UpdateRequest.UpdateType.APPEND_RANGE);

            return this;
        }

        public UpdateLogBuilder delete(UpdateLog updateLog) {
            int previousStart       = updateLog.getUpdateRequest().getStartPosition();
            int previousEnd         = updateLog.getUpdateRequest().getEndPosition();
            int currentStart        = updateLog.getUpdateRequest().getStartPosition();
            int currentEnd          = updateLog.getUpdateRequest().getEndPosition();

            this.updateRequest = new UpdateRequest.UpdateRequestBuilder().setContent("")
                    .setType(UpdateRequest.UpdateType.DELETE)
                    .setUserEmail(updateLog.getUpdateRequest().getUserEmail())
                    .setStartPosition(max(previousStart, currentStart))
                    .setEndPosition(min(previousEnd, currentEnd)).build();

            this.timestamp = updateLog.getTimestamp();

            return this;
        }

        public UpdateLogBuilder deleteRange(UpdateLog updateLog) {
            this.delete(updateLog);
            this.updateRequest.setType(UpdateRequest.UpdateType.DELETE_RANGE);

            return this;
        }

        public UpdateLog build() {
            return new UpdateLog(this);
        }
    }
}
