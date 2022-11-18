package docSharing.entities.document;


import docSharing.entities.Permission;
import docSharing.entities.User;

import java.util.HashMap;
import java.util.List;

public class Folder extends File{

    public Folder(int id,User user,File parent,String title,String url) {
        super(id, user,parent,title,url);
    }


}
