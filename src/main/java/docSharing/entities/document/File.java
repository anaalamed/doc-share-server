package docSharing.entities.document;
import docSharing.entities.User;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String url;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id", referencedColumnName = "id")
    private MetaData metadata;

    private File() {
    }

    public File(User owner, Folder parent, String title, String url) {
        this.url = url;
        this.metadata = new MetaData(this, parent, title, owner);
    }

    public String getUrl() {
        return url;
    }

    public MetaData getMetadata() {
        return metadata;
    }
}
