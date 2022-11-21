package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.Permission;
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
    public ResponseEntity<BaseResponse<Document>> create(@RequestParam int ownerId,
                                                         @RequestParam int parentId, @RequestParam String title) {
        logger.info("in create()");

        if(title.equals("")) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The title is empty!"));
        }
        logger.info("document: " + title + " created");
        return ResponseEntity.ok(BaseResponse.success(documentService.createDocument(ownerId, parentId, title)));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/updatePermission/{permission}")
    public ResponseEntity<BaseResponse<Void>> updatePermission(@RequestHeader int id, @RequestParam int ownerId,
                                                               @RequestParam int userId, @PathVariable("permission") String permission) {
        logger.info("in updatePermission()");

        Permission permissionType = Permission.valueOf(permission);
        if (documentService.updatePermission(id, ownerId, userId, permissionType)) {
            logger.info("owner: " + ownerId + " updated " + userId + " permission to: " + permission);
            return ResponseEntity.ok(BaseResponse.success(null));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Update Permission to user: " + userId + " failed"));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int id, @RequestParam int userId) {

        logger.info("in delete()");

        if(documentService.delete(id, userId)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "document was successfully deleted"));
        }
        else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Document deletion failed"));
        }
    }
}