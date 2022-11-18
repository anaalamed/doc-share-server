package docSharing.entities.document;


import docSharing.entities.Permission;
import docSharing.entities.User;

import java.util.HashMap;
import java.util.List;

public class Directory extends File{

    public Directory(int id, String url, HashMap<Permission, List<User>> authorized, MetaData metaData, List<UpdateLog> updateLogs) {
        super(id, url, authorized, metaData, updateLogs);
    }

}
