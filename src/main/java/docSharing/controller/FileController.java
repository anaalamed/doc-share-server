package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.FileType;
import docSharing.entities.document.Folder;
import docSharing.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static docSharing.utils.Utils.*;

@RestController
@CrossOrigin
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    private static final Logger logger = LogManager.getLogger(FileController.class.getName());

    public FileController() {
    }

    @RequestMapping(method = RequestMethod.POST, path="/create/{type}")
    public ResponseEntity<BaseResponse<String>> create(@RequestParam User owner, @RequestParam Folder parent,
                                                       @RequestParam String title, @RequestParam String url,
                                                       @PathVariable FileType type) {
        logger.info("in create");
        if(!isCreateValid(owner, title)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Create invalid!"));
        }
        switch (type){
            case DOCUMENT:
                fileService.createDocument(owner, parent, title, url);
                return ResponseEntity.ok(BaseResponse.success("document: "+title+"created"));
            case FOLDER:
                fileService.createFolder(owner, parent, title, url);
                return ResponseEntity.ok(BaseResponse.success("document: "+title+"created"));
            default:
                return ResponseEntity.badRequest().body(BaseResponse.failure("No match type"));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/delete/{type}")
    public ResponseEntity<BaseResponse<String>> delete(@RequestParam String url, @RequestParam User user, @PathVariable FileType type){
        logger.info("in delete");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        switch (type){
            case DOCUMENT:
                fileService.deleteDocument(url,user);
                return ResponseEntity.ok(BaseResponse.success("document in url: "+url+" deleted"));
            case FOLDER:
                fileService.deleteFolder(url,user);
                return ResponseEntity.ok(BaseResponse.success("folder in url: "+url+" deleted"));
            default:
                return ResponseEntity.badRequest().body(BaseResponse.failure("No match type"));
        }

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

}
