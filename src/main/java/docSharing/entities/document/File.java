package docSharing.entities.document;
import docSharing.entities.User;

import javax.persistence.*;
import java.nio.file.FileSystems;

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

    public File(User owner, Folder parent, String title) {
        this.metadata = new MetaData(this, parent, title, owner);
        generateUrl();
    }

    public String getUrl() {
        return url;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setParent(Folder parent) {
        this.metadata.setParent(parent);
        generateUrl();
    }

    public void setTitle(String title) {
        this.metadata.setTitle(title);
        generateUrl();
    }

    private void generateUrl() {
        String url = this.metadata.getTitle();
        File parent = this.metadata.getParent();

        while (parent != null) {
            url = parent.getMetadata().getTitle() + FileSystems.getDefault().getSeparator() + url;
            parent = parent.getMetadata().getParent();
        }

        this.url = url;
    }
}
