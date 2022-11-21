package docSharing.entities.document;

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

    public File(int ownerId, int parentId, String title) {
        this.metadata = new MetaData(this, parentId, title, ownerId);
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setParentId(int parentId) {
        this.metadata.setParentId(parentId);
    }

    public void setTitle(String title) {
        this.metadata.setTitle(title);
    }
}
