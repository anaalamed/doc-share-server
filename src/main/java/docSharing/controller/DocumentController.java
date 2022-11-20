package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Content;
import docSharing.entities.document.Document;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLDataException;

import static jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle.title;

@RestController
@CrossOrigin
@RequestMapping("/document")
public class DocumentController {
    @Autowired
    private  DocumentService documentService;

    private static Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public DocumentController() {
    }

    @RequestMapping(method = RequestMethod.POST)// should be PUT?
    public static void join(@RequestParam User user,@RequestParam String url){
        if(!isValidURL(url)) {
            logger.error("in join: Invalid url!");
        }else{
            documentService.join(user,url);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public static void leave(@RequestParam User user,@RequestParam String url) {
        if(!isValidURL(url)) {
            logger.error("in leave: Invalid url!");
        }else{
            documentService.leave(user,url);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public static BaseResponse<String> update(@RequestParam String url,@RequestParam UpdateMessage updateMessage){//TODO: maybe need to change the response type
        if(!isValidURL(url)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "in update: Invalid URL");
        }else{
            try{
                return BaseResponse.success(documentService.update(url, updateMessage));
            } catch (SQLDataException e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Error. No update was performed.", e);
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST) // should be PUT?
    public static BaseResponse<Document> create(RequestParam User owner, RequestParam  String title) {
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

    @RequestMapping(method = RequestMethod.DELETE)
    public static BaseResponse<Boolean> delete(@RequestParam String url, @RequestParam User user) {
        if(!isValidURL(url)) {
            logger.error("in delete: Invalid url!");
        }else if (documentService.delete(url, user)) {//TODO: delete implementation in service
                    return BaseResponse.success(true);
                }
       return BaseResponse.failure("Error. No deletion was performed.");
    }

    @RequestMapping(method = RequestMethod.POST)
    BaseResponse<User> updatePermission(@RequestParam String url,@RequestParam User user,@RequestParam Permission permission){
        if(!isValidURL(url)) {
            logger.error("in updatePermission: Invalid url!");
        }else {
            if (documentService.updatePermission(url, user, permission)) {//TODO: delete implementation in service
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
            logger.error(" Invalid url!");
            return false;
        } catch (URISyntaxException e) {
            logger.error(" Invalid url!");
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