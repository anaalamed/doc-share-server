package docSharing.entities.document;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UsersList;

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
    @Column(name = "users")
    @MapKeyEnumerated
    private Map<Permission, UsersList> authorized = new HashMap<>();

    @ElementCollection
    private final List<User> activeUsers;

    @ElementCollection
    private final List<UpdateLog> updateLogs;

    public Document(User owner, Folder parent, String title, String url) {
        super(owner, parent, title, url);
        this.content = new Content();

        for (Permission permission : Permission.values()) {
            this.authorized.put(permission, new UsersList());
        }

        this.authorized.get(Permission.OWNER).add(owner);
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

    public boolean hasPermission(User user, Permission permission) {
        return this.authorized.get(permission).contains(user);
    }

    public void addActiveUser(User user) {
        if (!this.activeUsers.contains(user)) {
            this.activeUsers.add(user);
        }
    }

    public void removeActiveUser(User user) {
        if (this.activeUsers.contains(user)) {
            this.activeUsers.remove(user);
        }
    }

    public void updatePermission(User user, Permission permission) {
        for (Permission permissionType : Permission.values()) {
            if (this.authorized.get(permissionType).contains(user)) {
                this.authorized.get(permissionType).remove(user);
            }
        }

        this.authorized.get(permission).add(user);
    }

    public boolean isActiveUser(User user) {
        return this.activeUsers.contains(user);
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