package docSharing.entities.document;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.Permission;
import docSharing.entities.UserIDList;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "document")
public class Document extends File {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id", referencedColumnName = "id")
    private Content content;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "authorized_users_mapping",
            joinColumns = {@JoinColumn(name = "document_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "users_list_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "permission")
    @Column(name = "authorized")
    @MapKeyEnumerated
    private Map<Permission, UserIDList> authorized = new HashMap<>();

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

        for (Permission permission : Permission.values()) {
            this.authorized.put(permission, new UserIDList());
        }

        this.authorized.get(Permission.OWNER).add(ownerId);
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

    public boolean hasPermission(int userId, Permission permission) {
        return this.authorized.get(permission).contains(userId);
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

    public void updatePermission(int userId, Permission permission) {
        for (Permission permissionType : Permission.values()) {
            if (this.authorized.get(permissionType).contains(userId)) {
                this.authorized.get(permissionType).remove(userId);
            }
        }

        this.authorized.get(permission).add(userId);
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