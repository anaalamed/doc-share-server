package docSharing.entities.document;

import docSharing.entities.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "folder")
public class Folder extends File {
    @ElementCollection
    private List<File> subFiles;

    public Folder() {
    }

    public Folder(User owner, Folder parent, String title) {
        super(owner, parent, title);
        this.subFiles = new ArrayList<>();
    }

    public List<File> getSubFiles() {
        return subFiles;
    }

    public void addSubFile(File file) {
        this.subFiles.add(file);
    }

    public void removeSubFile(File file) {
        this.subFiles.remove(file);
    }
}
