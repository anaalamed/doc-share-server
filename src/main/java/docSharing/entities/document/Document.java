package docSharing.entities.document;

import docSharing.entities.Permission;
import docSharing.entities.User;
import javax.persistence.*;
import java.util.HashMap;
import java.util.List;


public class Document extends File{

    private String content;

    public Document(int id, String url, HashMap<Permission, List<User>> authorized, MetaData metaData, List<UpdateLog> updateLogs, String content) {
        super(id, url, authorized, metaData, updateLogs);
        this.content = content;
    }


}
