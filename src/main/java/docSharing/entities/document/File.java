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
    private int id;
    @Column(unique=true)
    private String url;
    private HashMap<Permission, List<User>> authorized;
    private MetaData metaData;
    private final List<UpdateLog> updateLogs;

    private File(){
        this.authorized= new HashMap<>();
        this.updateLogs= new ArrayList<>();

    }
    public File(User user, File parent, String title, String url) {
        this();
        this.url=url;
        metaData = new MetaData (parent,title,user);
        authorized.put(Permission.OWNER,new ArrayList<>());
        authorized.get(Permission.OWNER).add(user);
    }
}
