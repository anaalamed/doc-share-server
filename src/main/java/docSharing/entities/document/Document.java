package docSharing.entities.document;

import docSharing.controller.request.UpdateRequest;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document")
public class Document extends File {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id", referencedColumnName = "id")
    private Content content;

    @Transient
    private final List<Integer> activeUsers;

    @ElementCollection
    private final List<UpdateLog> updateLogs;

    public Document() {
        super();
        this.activeUsers = new ArrayList<>();
        this.updateLogs = new ArrayList<>();
    }

    public Document(int ownerId, int parentId, String title) {
        super(ownerId, parentId, title);
        this.content = new Content();
        this.activeUsers = new ArrayList<>();
        this.updateLogs = new ArrayList<>();
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    private void addUpdateToLog(UpdateLog updateLog) {
        this.updateLogs.add(updateLog);
    }

    public void addActiveUser(int userId) {
        if (!this.activeUsers.contains(userId)) {
            this.activeUsers.add(userId);
        }
    }

    public void removeActiveUser(int userId) {
        if (this.activeUsers.contains(userId)) {
            this.activeUsers.remove(userId);
        }
    }

    public boolean isActiveUser(int userId) {
        return this.activeUsers.contains(userId);
    }

    public void updateContent(UpdateRequest updateRequest) {
        switch (updateRequest.getType()) {
            case APPEND:
                this.content.append(updateRequest.getContent(), updateRequest.getStartPosition());
                break;
            case DELETE:
                this.content.delete(updateRequest.getStartPosition(), updateRequest.getEndPosition());
                break;
            case APPEND_RANGE:
                this.content.appendRange(updateRequest.getContent(), updateRequest.getStartPosition(),
                        updateRequest.getEndPosition());
                break;
            case DELETE_RANGE:
                this.content.deleteRange(updateRequest.getStartPosition(), updateRequest.getEndPosition());
                break;
            default:
                throw new IllegalArgumentException("Unsupported update request type!");
        }

        addUpdateToLog(new UpdateLog(updateRequest, LocalDate.now()));
    }
}