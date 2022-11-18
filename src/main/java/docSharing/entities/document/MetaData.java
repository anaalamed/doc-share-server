package docSharing.entities.document;

import docSharing.entities.User;

import java.time.LocalDate;

public class MetaData {
    File parent;
    final LocalDate created;
    LocalDate lastUpdated;
    String title;
    final User createdBy;

    public MetaData(File parent, LocalDate created, LocalDate lastUpdated, String title, User createdBy) {
        this.parent = parent;
        this.created = created;
        this.lastUpdated = lastUpdated;
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
