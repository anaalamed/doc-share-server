package docSharing.entities.document;

import docSharing.entities.User;

import java.time.LocalDate;

public class UpdateLog {
    final String updateContent;
    final int position;
    final LocalDate timestamp;
    final User user;


    public UpdateLog(String updateContent, int position, LocalDate timestamp, User user) {
        this.updateContent = updateContent;
        this.position = position;
        this.timestamp = timestamp;
        this.user = user;
    }

    @Override
    public String toString() {
        return "UpdateLog{" +
                "updateContent='" + updateContent + '\'' +
                ", position=" + position +
                ", timestamp=" + timestamp +
                ", user=" + user +
                '}';
    }


}
