package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.StyledEditorKit;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class DocumentController {
    private static DocumentService documentService = new DocumentService();
    private static Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public static BaseResponse<Document> create(User owner, String title) {
        if (isUserNull(owner)) {
            logger.error("in create: NULL user trying to create file!");
        }
        if (title == "") {
            logger.info("in create: The document will be created without a title");
        }

        Document document = documentService.create(owner, title);//TODO: create implementation in service
        if (document != null) {
            return BaseResponse.success(document);
        } else {
            return BaseResponse.failure("Null document");
        }
    }

    public static BaseResponse<Boolean> delete(String url, User user) {
        boolean isDeleted=false;
        if (!isUserOwner(url, user)) {
            logger.error("in delete: -owner foreigner. No delete permission!");
        } else if(!isValidURL(url)) {
                logger.error("in delete: Invalid url!");
            }else {
                if (documentService.delete(url, user)) {//TODO: delete implementation in service
                    return BaseResponse.success(true);
                }
            }
       return BaseResponse.failure("Error. No deletion was performed.");
    }

    private static Boolean isUserOwner (String url, User user){
        //TODO:implement
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

}
/*API:
 - DocController()
 + join(User user, String url)
 + leave(User user, String url)
 + update(String url, UpdateMessage updateMessage)

 + BaseResponse<Document> create(User owner)
 + BaseResponse<Void> delete(String url, User user)
 + BaseResponse<User> updatePermission(String url, User user, Permission permission)


 * if socket enables updating and creating entities like REST, then all methods should work with socket. if not: we need to divide API methods to different controllers.*/