package docSharing.controller;

import docSharing.controller.request.UpdateRequest;
import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.Folder;
import docSharing.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLDataException;


@RestController
@CrossOrigin
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private FileService fileService;

    private static final Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public DocumentController() {
    }

    @RequestMapping(method = RequestMethod.POST, path= "/join")
    public ResponseEntity<BaseResponse<String>> join(@RequestParam User user,@RequestParam String url){
        logger.info("in join");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        fileService.join(user,url);
        return ResponseEntity.ok(BaseResponse.success("user"+ user.getName() +"join to:"+ url));
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/leave")
    public ResponseEntity<BaseResponse<String>> leave(@RequestParam User user,@RequestParam String url) {
        logger.info("in leave");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        fileService.leave(user,url);
        return ResponseEntity.ok(BaseResponse.success("user"+ user.getName() +"leave:"+ url));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/update/{updateMessage}", params="url")
    public  ResponseEntity<BaseResponse<String>> update(@RequestParam String url,@PathVariable("updateMessage") UpdateRequest updateRequest){
        logger.info("in update");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        fileService.update(url, updateRequest);
        return ResponseEntity.ok(BaseResponse.success("update message:"+updateRequest.getContent()+"in url:"+url));
    }

    @RequestMapping(method = RequestMethod.POST, path="/create")
    public ResponseEntity<BaseResponse<String>> create(@RequestParam User owner, @RequestParam Folder parent,
                                                         @RequestParam String title, @RequestParam String url) {
        logger.info("in create");
        if(!isCreateValid(owner, title)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Create invalid!"));
        }
        fileService.createDocument(owner, parent, title, url);
        return ResponseEntity.ok(BaseResponse.success("document: "+title+"created"));
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/delete")
    public ResponseEntity<BaseResponse<String>> delete(@RequestParam String url, @RequestParam User user) {
        logger.info("in delete");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        fileService.delete(url,user);
        return ResponseEntity.ok(BaseResponse.success("document in url: "+url+" deleted"));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/updatePermission/{permission}")
    public ResponseEntity<BaseResponse<String>> updatePermission(@RequestParam String url, @RequestParam User owner,
                                                                 @RequestParam User user, @PathVariable("permission") Permission permission){
        logger.info("in updatePermission");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        fileService.updatePermission(url,owner, user, permission);
        return ResponseEntity.ok(BaseResponse.success("owner: "+owner.getName()+"update "+ user.getName()+"permission to: "+ permission));
    }


    private  Boolean isUserNull (User user){
        return user.getId() == 0;
    }
    private  boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            logger.error(" Invalid url!");
            return false;
        }
    }
    public  boolean isCreateValid(User owner, String title) {
        if (isUserNull(owner)) {
            logger.error("in create: NULL user trying to create file!");
            return false;
        }
        if (title.equals("")) {
            logger.info("in create: The document will be created without a title");
            return false;
        }
        return true;
    }

}