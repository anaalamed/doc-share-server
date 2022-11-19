package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Content;
import docSharing.entities.document.Document;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class DocumentController {
    private static DocumentService documentService = new DocumentService();
    private static Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public DocumentController() {
    }
    public static void join(User user, String url){
        if(!isValidURL(url)) {
            logger.error("in join: Invalid url!");
        }else{
            documentService.join(user,url);
        }
    }

    public static void leave(User user, String url) {
        if(!isValidURL(url)) {
            logger.error("in leave: Invalid url!");
        }else{
            documentService.leave(user,url);
        }
    }

    public static BaseResponse<Boolean> update(String url, Content updateMessage){
        if(!isValidURL(url)) {
            logger.error("in update: Invalid url!");
        }else if (documentService.update(url, updateMessage)) {//TODO: update implementation in service
            return BaseResponse.success(true);
        }
        return BaseResponse.failure("Error. No update was performed.");
    }

    public static BaseResponse<Document> create(User owner, String title) {
        try{
            isCreateValid(owner, title);
        }catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        Document document = documentService.create(owner, title);//TODO: create implementation in service
        if (document != null) {
            return BaseResponse.success(document);
        } else {
            return BaseResponse.failure("Null document");
        }
    }

    public static BaseResponse<Boolean> delete(String url, User user) {
        if(!isValidURL(url)) {
            logger.error("in delete: Invalid url!");
        }else if (documentService.delete(url, user)) {//TODO: delete implementation in service
                    return BaseResponse.success(true);
                }
       return BaseResponse.failure("Error. No deletion was performed.");
    }

    BaseResponse<User> updatePermission(String url, User user, Permission permission){
        if(!isValidURL(url)) {
            logger.error("in updatePermission: Invalid url!");
        }else {
            if (documentService.updatePermission(url, user,permission)) {//TODO: delete implementation in service
                return BaseResponse.success(user);
            }
        }
        return BaseResponse.failure("Error. No update was performed.");
    }


    private static Boolean isUserNull (User user){
        if (user.getId() == 0)
            return true;
        return false;
    }
    private static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
    public static boolean isCreateValid(User owner, String title) {
        if (isUserNull(owner)) {
            logger.error("in create: NULL user trying to create file!");
            return false;
        }
        if (title == "") {
            logger.info("in create: The document will be created without a title");
            return false;
        }
        return true;
    }

}
/*API:
 V - DocController()
 + join(User user, String url)
 + leave(User user, String url)
 + update(String url, UpdateMessage updateMessage)

 V + BaseResponse<Document> create(User owner)
 V + BaseResponse<Void> delete(String url, User user)
 V + BaseResponse<User> updatePermission(String url, User user, Permission permission)


 * if socket enables updating and creating entities like REST, then all methods should work with socket. if not: we need to divide API methods to different controllers.*/