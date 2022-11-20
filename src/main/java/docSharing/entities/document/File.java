package docSharing.entities.document;

import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UsersList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "file")
public abstract class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private String url;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "authorized_users_mapping",
            joinColumns = {@JoinColumn(name = "file_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "users_list_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "permission")
    @Column(name = "users")
    @MapKeyEnumerated
    private Map<Permission, UsersList> authorized = new HashMap<>();
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id", referencedColumnName = "id")
    private MetaData metadata;
    @ElementCollection
    private final List<UpdateLog> updateLogs;
    @ElementCollection
    private final List<User> activeUsers;

    private File() {
        for (Permission permission : Permission.values()) {
            this.authorized.put(permission, new UsersList());
        }

        this.updateLogs = new ArrayList<>();
        this.activeUsers = new ArrayList<>();
    }

    public File(User user, File parent, String title, String url) {
        this();
        this.url = url;
        this.metadata = new MetaData(this, parent, title, user);
        this.authorized.get(Permission.OWNER).add(user);
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Map<Permission, UsersList> getAuthorized() {
        return authorized;
    }

    public MetaData getMetadata() {
        return metadata;
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
