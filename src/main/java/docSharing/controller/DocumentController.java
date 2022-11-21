package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.*;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    private static final Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public DocumentController() {
    }

    @RequestMapping(method = RequestMethod.POST, path="/create")
    public ResponseEntity<BaseResponse<Document>> create(@RequestParam User owner,
                                                         @RequestParam Folder parent, @RequestParam String title) {
        logger.info("in create");
        if(!owner.equals(null)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("NULL user trying to create document!"));
        }
        if(title.equals("")) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The title is empty!"));
        }
        logger.info("document: " + title + " created");
        return ResponseEntity.ok(BaseResponse.success(documentService.createDocument(owner, parent, title)));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/updatePermission/{permission}")
    public ResponseEntity<BaseResponse<User>> updatePermission(@RequestHeader int id, @RequestParam User owner,
                                                                 @RequestParam User user, @PathVariable("permission") Permission permission){
        logger.info("in updatePermission");
        if(documentService.updatePermission(id,owner, user, permission)) {
            logger.info("owner: "+owner.getName()+"update "+ user.getName()+"permission to: "+ permission);
            return ResponseEntity.ok(BaseResponse.success(user));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Update Permission to"+user.getName()+" failed"));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int id, @RequestParam User user){
        logger.info("in delete");
        if(documentService.delete(id,user)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "document deleted"));
        }
        else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The deletion failed"));
        }
    }
}