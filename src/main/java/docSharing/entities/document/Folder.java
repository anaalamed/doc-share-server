package docSharing.entities.document;
import docSharing.entities.User;

import java.util.ArrayList;
import java.util.List;


public class Folder extends File {
    private List<File> subFiles;

    public Folder(User owner, File parent, String title, String url) {
        super(owner, parent, title, url);
        this.subFiles = new ArrayList<>();
    }

    public List<File> getSubFiles() {
        return subFiles;
    }

    public void addSubFile(File file) {
        this.subFiles.add(file);
    }
}
