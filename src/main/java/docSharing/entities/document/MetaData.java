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
    @JoinColumn(name = "folder_id")
    private Folder parent;
    @Column(name = "created")
    private final LocalDate created;
    @Column(name = "last_updated")
    private LocalDate lastUpdated;
    @Column(name = "title")
    private String title;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User createdBy;
    @OneToOne(mappedBy = "metadata")
    private File file;

    public MetaData(File file, Folder parent, String title, User createdBy) {
        this.file = file;
        this.parent = parent;
        this.created = LocalDate.now();
        this.lastUpdated =  LocalDate.now();
        this.title = title;
        this.createdBy = createdBy;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
