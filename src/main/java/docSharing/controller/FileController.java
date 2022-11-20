package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
import docSharing.entities.User;
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

}
