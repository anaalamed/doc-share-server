package docSharing.entities.document;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "metadata")
public class MetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "parent_id") //TODO: ManyToOne
    private int parentId;
    @Column(name = "created")
    private final LocalDate created;
    @Column(name = "last_updated")
    private LocalDate lastUpdated;
    @Column(name = "title")
    private String title;
    @Column(name = "owner_id")
    private int owner_id;
    @OneToOne(mappedBy = "metadata")
    private File file;

    private MetaData() {
        this.created = LocalDate.now();
        this.lastUpdated =  LocalDate.now();
    }

    public MetaData(File file, int parentId, String title, int createdBy) {
        this();
        this.file = file;
        this.parentId = parentId;
        this.title = title;
        this.owner_id = createdBy;
    }

    public int getParentId() {
        return parentId;
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

    public int getOwner_id() {
        return owner_id;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    protected void setParentId(int parentId) {
        this.parentId = parentId;
    }

    protected void setTitle(String title) {
        this.title = title;
    }
}
