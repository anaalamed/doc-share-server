package docSharing.controller;

import docSharing.controller.request.ShareRequest;
import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.entities.document.*;
import docSharing.entities.permission.Permission;
import docSharing.service.DocumentService;
import docSharing.service.PermissionService;
import docSharing.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

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

        Document document = documentService.createDocument(ownerId, parentId, title);
        if (document != null) {
            permissionService.addPermission(document.getId(), ownerId, Permission.OWNER);
        }

        return ResponseEntity.ok(BaseResponse.success(document));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/share")
    public ResponseEntity<BaseResponse<Void>> share(@RequestBody ShareRequest shareRequest) {
        logger.info("in share()");

        if (!permissionService.isAuthorized(shareRequest.getDocumentID(), shareRequest.getOwnerID(), Permission.EDITOR)) {
            return getNoEditPermissionResponse(shareRequest.getOwnerID());
        }

        boolean allSucceed = true;

        List<User> users = retrieveShareRequestUsers(shareRequest);
        for (User user : users) {
            permissionService.updatePermission(shareRequest.getDocumentID(), user.getId(), shareRequest.getPermission());

            if (shareRequest.isNotify()) {
                allSucceed = allSucceed &&
                        permissionService.notifyShareByEmail
                                (shareRequest.getDocumentID(), user.getEmail(), shareRequest.getPermission());
            }
        }

        if (allSucceed) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "Share by email succeed for all users"));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Share by email failed for some users"));
        }
    }

    @RequestMapping(method = RequestMethod.GET, path="/getUrl")
    public ResponseEntity<BaseResponse<String>> getUrl(@RequestHeader int documentId) {
        logger.info("in getUrl()");

        return ResponseEntity.ok(BaseResponse.success(documentService.getUrl(documentId)));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/setParent")
    public ResponseEntity<BaseResponse<Void>> setParent(@RequestHeader int documentId, @RequestHeader int userId,
                                                        @RequestParam int parentId) {
        logger.info("in setParent()");

        if (!permissionService.isAuthorized(documentId, userId, Permission.EDITOR)) {
            return getNoEditPermissionResponse(userId);
        }

        try {
            documentService.setParent(documentId, parentId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }

        return ResponseEntity.ok(BaseResponse.noContent(true, "Parent ID is now: " + parentId));
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/setTitle")
    public ResponseEntity<BaseResponse<Document>> setTitle(@RequestHeader int documentId, @RequestHeader int userId,
                                                        @RequestParam String title) {
        logger.info("in setTitle()");

        if (!permissionService.isAuthorized(documentId, userId, Permission.EDITOR)) {
            return getNoEditPermissionResponse(userId);
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(documentService.setTitle(documentId, title)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int documentId, @RequestParam int userId) {

        logger.info("in delete()");

        if (!permissionService.isAuthorized(documentId, userId, Permission.EDITOR)) {
            return getNoEditPermissionResponse(userId);
        }

        if (documentService.delete(documentId)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "document was successfully deleted"));
        }
        else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Document deletion failed"));
        }
    }

    private List<User> retrieveShareRequestUsers(ShareRequest shareRequest) {
        List<User> users = new ArrayList<>();

        for (String email : shareRequest.getEmails()) {
            Optional<User> user = userService.getByEmail(email);
            if (!user.isPresent()) {
                logger.warn("Shared via email failed - user: " + email + " does not exist!");
                continue;
            }

            users.add(user.get());
        }

        return users;
    }

    private <T> ResponseEntity<BaseResponse<T>> getNoEditPermissionResponse(int userId) {
        return ResponseEntity.badRequest().body(BaseResponse.failure(
                String.format("User: %d does not have edit permission for this document!", userId)));
    }
}