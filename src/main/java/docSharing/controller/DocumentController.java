package docSharing.controller;

import docSharing.controller.request.ShareRequest;
import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.entities.document.*;
import docSharing.service.DocumentService;
import docSharing.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    @Autowired
    private UserService userService;

    private static final Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public DocumentController() {
    }

    @RequestMapping(method = RequestMethod.POST, path="/create")
    public ResponseEntity<BaseResponse<Document>> create(@RequestParam int ownerId,
                                                         @RequestParam int parentId, @RequestParam String title) {
        logger.info("in create()");

        if(title.equals("")) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Title cannot be empty!"));
        }
        logger.info("document: " + title + " created");
        return ResponseEntity.ok(BaseResponse.success(documentService.createDocument(ownerId, parentId, title)));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/share")
    public ResponseEntity<BaseResponse<Void>> share(@RequestBody ShareRequest shareRequest) {
        logger.info("in share()");

        if (!documentService.hasEditPermission(shareRequest.getDocumentID(), shareRequest.getOwnerID())) {
            return getNoEditPermissionResponse(shareRequest.getOwnerID());
        }

        if (shareHandler(shareRequest)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "Share by email succeed for all users"));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Share by email failed for some users"));
        }
    }

    @RequestMapping(method = RequestMethod.GET, path="/getUrl")
    public ResponseEntity<BaseResponse<String>> getUrl(@RequestHeader int documentId) {
        logger.info("in getUrl()");

        return ResponseEntity.ok(BaseResponse.success(documentService.generateUrl(documentId)));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/setParent")
    public ResponseEntity<BaseResponse<Void>> setParent(@RequestHeader int documentId, @RequestHeader int userId,
                                                        @RequestParam int parentId) {
        logger.info("in setParent()");

        if (!documentService.hasEditPermission(documentId, userId)) {
            return getNoEditPermissionResponse(userId);
        }

        try {
            documentService.setParent(documentId, parentId, userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }

        return ResponseEntity.ok(BaseResponse.noContent(true, "Parent ID is now: " + parentId));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/setTitle")
    public ResponseEntity<BaseResponse<Document>> setTitle(@RequestHeader int documentId, @RequestHeader int userId,
                                                        @RequestParam String title) {
        logger.info("in setTitle()");

        if (!documentService.hasEditPermission(documentId, userId)) {
            return getNoEditPermissionResponse(userId);
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(documentService.setTitle(documentId, title, userId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int documentId, @RequestParam int userId) {

        logger.info("in delete()");

        if (!documentService.hasEditPermission(documentId, userId)) {
            return getNoEditPermissionResponse(userId);
        }

        if (documentService.delete(documentId, userId)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "document was successfully deleted"));
        }
        else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Document deletion failed"));
        }
    }

    private boolean shareHandler(ShareRequest shareRequest) {
        boolean allSucceed = true;

        for (String email : shareRequest.getEmails()) {
            Optional<User> user = userService.getByEmail(email);
            if (!user.isPresent()) {
                allSucceed = false;
                logger.warn("Shared via email failed - user: " + email + " does not exist!");
                continue;
            }

            shareRequest.addUser(user.get());
        }

        return allSucceed && documentService.share(shareRequest);
    }

    private <T> ResponseEntity<BaseResponse<T>> getNoEditPermissionResponse(int userId) {
        return ResponseEntity.badRequest().body(BaseResponse.failure(
                String.format("User: %d does not have edit permission for this document!", userId)));
    }
}