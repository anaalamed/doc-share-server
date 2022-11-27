package docSharing.entities.document;
import docSharing.entities.User;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id", referencedColumnName = "id")
    private MetaData metadata;

    protected File() {
    }

    public File(User owner, Folder parent, String title) {
        this.metadata = new MetaData(this, parent, title, owner);
    }

    public int getId() {
        return id;
    }

    public int getId() {
        return id;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setTitle(String title) {
        this.metadata.setTitle(title);
    }
}
