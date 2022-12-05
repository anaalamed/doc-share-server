package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.file.Folder;
import docSharing.service.AuthService;
import docSharing.service.FolderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/folder")
public class FolderController {
    @Autowired
    private FolderService folderService;
    @Autowired
    private AuthService authService;
    private static final Logger logger = LogManager.getLogger(FolderController.class.getName());

    public FolderController() {
    }

    /**
     * create new folder
     * @param token
     * @param ownerId
     * @param parentId
     * @param title
     * @return Folder
     */
    @RequestMapping(method = RequestMethod.POST, path="/create")
    public ResponseEntity<BaseResponse<Folder>> create(@RequestHeader String token, @RequestHeader int ownerId,
                                                       @RequestParam int parentId, @RequestParam String title) {
        logger.info("in create()");

        if (!authService.isAuthenticated(ownerId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(folderService.createFolder(ownerId, parentId, title)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    /**
     * change folder parent to another parent.
     * @param token
     * @param folderId
     * @param userId
     * @param parentId
     * @return savedFolder
     */
    @RequestMapping(method = RequestMethod.PATCH, path="/setParent")
    public ResponseEntity<BaseResponse<Folder>> setParent(@RequestHeader String token, @RequestHeader int folderId,
                                                          @RequestHeader int userId, @RequestParam int parentId) {
        logger.info("in setParent()");

        if (!authService.isAuthenticated(userId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(folderService.setParent(folderId, parentId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    /**
     * set new title to folder
     * @param token
     * @param folderId
     * @param userId
     * @param title
     * @return updated folder
     */
    @RequestMapping(method = RequestMethod.PATCH, path="/setTitle")
    public ResponseEntity<BaseResponse<Folder>> setTitle(@RequestHeader String token, @RequestHeader int folderId,
                                                         @RequestHeader int userId, @RequestParam String title) {
        logger.info("in setTitle()");

        if (!authService.isAuthenticated(userId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        try {
            return ResponseEntity.ok(BaseResponse.success(folderService.setTitle(folderId, title)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    /**
     * delete folder from DB
     * @param folderId
     * @param userId
     * @param token
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int folderId, @RequestHeader int userId,
                                                     @RequestHeader String token) {
        logger.info("in delete()");

        if (!authService.isAuthenticated(userId, token)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User is not logged-in!"));
        }

        if (folderService.delete(folderId)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "Folder was successfully deleted"));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Folder deletion failed"));
        }
    }
}
