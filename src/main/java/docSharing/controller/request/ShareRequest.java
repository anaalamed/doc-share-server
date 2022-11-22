package docSharing.controller.request;
import docSharing.entities.Permission;
import docSharing.entities.User;

import java.util.ArrayList;
import java.util.List;


public class ShareRequest {
    private int documentID;
    private int ownerID;
    private List<String> emails;
    private Permission permission;
    private boolean notify;
    private List<User> users = new ArrayList<>();

    public ShareRequest() {
    }

    public ShareRequest(int documentID, int ownerID, List<String> emails, Permission permission, boolean notify) {
        this.documentID = documentID;
        this.ownerID = ownerID;
        this.emails = emails;
        this.permission = permission;
        this.notify = notify;
    }

    public int getDocumentID() {
        return documentID;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public List<String> getEmails() {
        return emails;
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean isNotify() {
        return notify;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }
}