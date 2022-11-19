package docSharing.entities.document;

import docSharing.entities.User;

import java.time.LocalDate;

public class MetaData {
    private File parent;
    private final LocalDate created;
    private LocalDate lastUpdated;
    private String title;
    private final User createdBy;

    public MetaData(File parent, String title, User createdBy) {
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
