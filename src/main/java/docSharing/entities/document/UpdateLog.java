package docSharing.entities.document;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.User;

import java.time.LocalDate;

public class UpdateLog {
    private UpdateRequest updateRequest;
    private LocalDate timestamp;

    public UpdateLog(UpdateRequest updateRequest, LocalDate timestamp) {
        this.updateRequest = updateRequest;
        this.timestamp = timestamp;
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
}
