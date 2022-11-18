package docSharing.entities.document;

import docSharing.entities.Permission;
import docSharing.entities.User;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name="file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;

    @Column(unique=true)
    private String url;

    private HashMap<Permission, List<User>> authorized;
    private MetaData metaData;
    private final List<UpdateLog> updateLogs;

    //empty constructor
    public File(){
        this.id=0;
        this.updateLogs=null;
    }
    public File(int id, String url, HashMap<Permission, List<User>> authorized, MetaData metaData, List<UpdateLog> updateLogs) {
        this.id = id;
        this.url = url;
        this.authorized = authorized;
        this.metaData = metaData;
        this.updateLogs = updateLogs;
    }
}
