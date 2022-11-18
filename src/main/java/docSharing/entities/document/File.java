package docSharing.entities.document;

import docSharing.entities.Permission;
import docSharing.entities.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name="file")
public abstract class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final int id;
    @Column(unique=true)
    private String url;
    private HashMap<Permission, List<User>> authorized;
    private MetaData metaData;
    private final List<UpdateLog> updateLogs;

    private List<User> owners;
    private List<User> editors;
    private List<User> viewers;

    //empty constructor
    public File(){
        this.id=0;
        this.updateLogs=null;
    }
    public File(int id, User user, File parent, String title, String url) {
        this.id = id;
        this.url=url;
        updateLogs= new ArrayList<>();
        authorized= new HashMap<>();
        owners=new ArrayList<>();
        owners.add(user);
        authorized.put(Permission.OWNER,owners);
        metaData = new MetaData (parent,title,user);
    }
}
