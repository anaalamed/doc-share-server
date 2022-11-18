package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DocumentController {
    private static DocumentService documentService=new DocumentService();
    private static Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public static BaseResponse<Document>  create(User owner,String title){
        if(isUserNull(owner)){
            logger.error("in create: NULL user trying to create file!");
        }
        if(title==""){
            logger.info("in create: The document will be created without a title");
        }

       Document document=documentService.create( owner,title);//TODO: create implementation in service
        if (document!= null){
            return BaseResponse.success(document);
        }else{
            return BaseResponse.failure("Null document");
        }

    }

    private static Boolean isUserNull(User user){
        if(user.getId()==0)
            return true;
        return false;
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