package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.FileType;
import docSharing.entities.document.Folder;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static docSharing.utils.Utils.isCreateValid;
import static docSharing.utils.Utils.isValidURL;

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
    public ResponseEntity<BaseResponse<Document>> create(@RequestHeader int id, @RequestParam User owner,
                                                         @RequestParam Folder parent, @RequestParam String title) {
        logger.info("in create");
        if(!isCreateValid(owner, title)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Create invalid!"));
        }
        return ResponseEntity.ok(BaseResponse.success(
                true,
                "document: "+title+"created",
                documentService.create(owner, parent, title, id)));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/updatePermission/{permission}")
    public ResponseEntity<BaseResponse<User>> updatePermission(@RequestHeader int id, @RequestParam User owner,
                                                                 @RequestParam User user, @PathVariable("permission") Permission permission){
        logger.info("in updatePermission");

        return ResponseEntity.ok(BaseResponse.success(
                true,
                "owner: "+owner.getName()+"update "+ user.getName()+"permission to: "+ permission,
                documentService.updatePermission(id,owner, user, permission)));
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int id, @RequestParam User user){
        logger.info("in delete");
        documentService.delete(id,user);
        return ResponseEntity.ok(BaseResponse.noContent(true,"document deleted"));
    }


}