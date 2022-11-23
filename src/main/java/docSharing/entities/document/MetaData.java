package docSharing.entities.document;

import docSharing.entities.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "metadata")
public class MetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    private Folder parent;
    @Column(name = "created")
    private final LocalDate created;
    @Column(name = "last_updated")
    private LocalDate lastUpdated;
    @Column(name = "title")
    private String title;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;
    @OneToOne(mappedBy = "metadata")
    private File file;

    private MetaData() {
        this.created = LocalDate.now();
        this.lastUpdated =  LocalDate.now();
    }

    public MetaData(File file, Folder parent, String title, User owner) {
        this();
        this.file = file;
        this.parent = parent;
        this.title = title;
        this.owner = owner;
    }

    public Folder getParent() {
        return parent;
    }

    public LocalDate getCreated() {
        return created;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public String getTitle() {
        return title;
    }

    public User getOwner() {
        return owner;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    protected void setTitle(String title) {
        this.title = title;
    }
}
