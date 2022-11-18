package docSharing.entities.document;

import docSharing.entities.Permission;
import docSharing.entities.User;
import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name="document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Column(unique=true)
    private String url;
    private String content;
    private HashMap<Permission, List<User>> authorized;
    private MetaData metaData;
    private final List<UpdateLog> updateLogs;

    public Document() {
        this.updateLogs = null;
        id = 0;
    }

    public Document(int id, MetaData metaData, String url, String content, HashMap<Permission, List<User>> authorized, MetaData metaData1, List<UpdateLog> updateLogs) {
        this.id = id;
        this.url = url;
        this.content = content;
        this.authorized = authorized;
        this.metaData = metaData1;
        this.updateLogs = updateLogs;
    }
    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public HashMap<Permission, List<User>> getAuthorized() {
        return authorized;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public List<UpdateLog> getUpdateLogs() {
        return updateLogs;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthorized(HashMap<Permission, List<User>> authorized) {
        this.authorized = authorized;
    }


}
