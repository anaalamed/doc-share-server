package docSharing.entities.permission;

import docSharing.entities.User;
import docSharing.entities.document.Document;

import javax.persistence.*;

@Entity
@Table(name = "authorized_users")
public class Authorization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Enumerated
    @Column(name = "permission")
    private Permission permission;

    public Authorization() {
    }

    public Authorization(Document document, User user, Permission permission) {
        this.document = document;
        this.user = user;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public User getUser() {
        return user;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
