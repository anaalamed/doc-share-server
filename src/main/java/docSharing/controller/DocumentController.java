package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.Folder;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static docSharing.utils.Utils.isCreateValid;

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
        if(!isCreateValid(owner, title)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Create invalid!"));
        }
        return ResponseEntity.ok(BaseResponse.success(
                true,
                "document: "+title+"created",
                documentService.createDocument(owner, parent, title)));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/updatePermission/{permission}")
    public ResponseEntity<BaseResponse<User>> updatePermission(@RequestHeader int id, @RequestParam User owner,
                                                                 @RequestParam User user, @PathVariable("permission") Permission permission){
        logger.info("in updatePermission");
        if(documentService.updatePermission(id,owner, user, permission)) {
            return ResponseEntity.ok(BaseResponse.success(true,
                    "owner: "+owner.getName()+"update "+ user.getName()+"permission to: "+ permission,
                    user));
        }
        else {
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