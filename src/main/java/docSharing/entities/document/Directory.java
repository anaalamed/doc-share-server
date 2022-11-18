package docSharing.entities.document;


import docSharing.entities.Permission;
import docSharing.entities.User;

import java.util.HashMap;
import java.util.List;

public class Directory {
    private String url;
    private HashMap<Permission, List<User>> authorized;
    private MetaData metaData;
    private final List<UpdateLog> updateLogs;

    public Directory(String url, HashMap<Permission, List<User>> authorized, MetaData metaData, List<UpdateLog> updateLogs) {
        this.url = url;
        this.authorized = authorized;
        this.metaData = metaData;
        this.updateLogs = updateLogs;
    }
}
