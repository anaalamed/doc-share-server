package docSharing.controller;

import docSharing.controller.request.ShareRequest;
import docSharing.controller.response.BaseResponse;
import docSharing.entities.DTO.UserDTO;
import docSharing.entities.file.*;
import docSharing.entities.permission.Permission;
import docSharing.service.AuthService;
import docSharing.service.DocumentService;
import docSharing.service.PermissionService;
import docSharing.service.UserService;
import docSharing.utils.Utils;
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
    private AuthService authService;
    @Autowired
    private PermissionService permissionService;
    private static final Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public DocumentController() {
    }

    @RequestMapping(method = RequestMethod.POST, path="/create")
    public ResponseEntity<BaseResponse<Document>> create(@RequestHeader String token, @RequestHeader int ownerId,
                                                         @RequestParam int parentId, @RequestParam String title) {
        logger.info("in create()");

        if (!authService.isAuthenticated(ownerId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        try {
            Document document = documentService.createDocument(ownerId, parentId, title);

            if (document != null) {
                permissionService.addPermission(document.getId(), ownerId, Permission.OWNER);
                return ResponseEntity.ok(BaseResponse.success(document));
            } else {
                return ResponseEntity.badRequest().body(BaseResponse.failure("Error occurred while trying to create a document"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/share")
    public ResponseEntity<BaseResponse<Void>> share(@RequestHeader String token, @RequestBody ShareRequest shareRequest) {
        logger.info("in share()");

        if (!authService.isAuthenticated(shareRequest.getOwnerID(), token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        if (!permissionService.isAuthorized(shareRequest.getDocumentID(), shareRequest.getOwnerID(), Permission.EDITOR)) {
            return Utils.getNoEditPermissionResponse(shareRequest.getOwnerID());
        }

        boolean allSucceed = true;
        for (UserDTO user : retrieveShareRequestUsers(shareRequest)) {
            permissionService.updatePermission(shareRequest.getDocumentID(), user.getId(), shareRequest.getPermission());

            if (shareRequest.isNotify()) {
                allSucceed = allSucceed && documentService.notifyShareByEmail
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
    public ResponseEntity<BaseResponse<Document>> setParent(@RequestHeader int documentId, @RequestHeader int userId,
                                                            @RequestHeader String token, @RequestParam int parentId) {
        logger.info("in setParent()");

        if (!authService.isAuthenticated(userId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        if (!permissionService.isAuthorized(documentId, userId, Permission.EDITOR)) {
            return Utils.getNoEditPermissionResponse(userId);
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(documentService.setParent(documentId, parentId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/setTitle")
    public ResponseEntity<BaseResponse<Document>> setTitle(@RequestHeader int documentId, @RequestHeader int userId,
                                                           @RequestHeader String token, @RequestParam String title) {
        logger.info("in setTitle()");

        if (!authService.isAuthenticated(userId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        if (!permissionService.isAuthorized(documentId, userId, Permission.EDITOR)) {
            return Utils.getNoEditPermissionResponse(userId);
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(documentService.setTitle(documentId, title)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int documentId, @RequestHeader String token,
                                                     @RequestHeader int userId) {

        logger.info("in delete()");

        if (!authService.isAuthenticated(userId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        if (!permissionService.isAuthorized(documentId, userId, Permission.EDITOR)) {
            return Utils.getNoEditPermissionResponse(userId);
        }

        if (documentService.delete(documentId)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "document was successfully deleted"));
        }
        else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Document deletion failed"));
        }
    }

    @RequestMapping(method = RequestMethod.POST, path="/import")
    public ResponseEntity<BaseResponse<Document>> importFile(@RequestHeader String token, @RequestHeader int ownerId,
                                                             @RequestParam String filePath, @RequestParam int parentId) {

        if (!authService.isAuthenticated(ownerId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(documentService.importFile(filePath, ownerId, parentId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.GET, path="/export")
    public ResponseEntity<BaseResponse<Void>> exportFile(@RequestHeader int documentId, @RequestHeader String token,
                                                         @RequestHeader int userId) {

        if (!authService.isAuthenticated(userId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        documentService.exportFile(documentId);
        return ResponseEntity.ok(BaseResponse.noContent(true, "Document was exported successfully."));
    }

    private List<UserDTO> retrieveShareRequestUsers(ShareRequest shareRequest) {
        List<UserDTO> users = new ArrayList<>();

        for (String email : shareRequest.getEmails()) {
            Optional<UserDTO> user = userService.getByEmail(email);
            if (!user.isPresent()) {
                logger.warn("Shared via email failed - user: " + email + " does not exist!");
                continue;
            }

            users.add(user.get());
        }

        return users;
    }
}