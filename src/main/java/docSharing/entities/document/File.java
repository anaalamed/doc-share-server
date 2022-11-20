package docSharing.entities.document;

import docSharing.entities.Permission;
import docSharing.entities.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "file")
public abstract class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private String url;
    private HashMap<Permission, List<User>> authorized;
    private MetaData metaData;
    private final List<UpdateLog> updateLogs;
    private final List<User> activeUsers;

    private File() {
        this.authorized = new HashMap<>();
        for (Permission permission : Permission.values()) {
            this.authorized.put(permission, new ArrayList<>());
        }

        this.updateLogs = new ArrayList<>();
        this.activeUsers = new ArrayList<>();
    }

    public File(User user, File parent, String title, String url) {
        this();
        this.url = url;
        this.metaData = new MetaData(parent, title, user);
        this.authorized.get(Permission.OWNER).add(user);
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public HashMap<Permission, List<User>> getAuthorized() {
        return authorized;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public List<UpdateLog> getUpdateLogs() {
        return updateLogs;
    }

    public List<User> getActiveUsers() {
        return activeUsers;
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
}
