package docSharing.entities.permission;

import javax.persistence.*;

@Entity
@Table(name = "authorized_users")
public class Authorization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "document_id")
    private int documentId;

    @Column(name = "user_id")
    private int userId;

    @Enumerated
    @Column(name = "permission")
    private Permission permission;

    public Authorization() {
    }

    public Authorization(int documentId, int userId, Permission permission) {
        this.documentId = documentId;
        this.userId = userId;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public int getDocumentId() {
        return documentId;
    }

    public int getUserId() {
        return userId;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
