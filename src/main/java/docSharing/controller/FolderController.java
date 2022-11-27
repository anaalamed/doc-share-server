package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.file.Folder;
import docSharing.service.FolderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/folder")
public class FolderController {
    @Autowired
    private FolderService folderService;

    private static final Logger logger = LogManager.getLogger(FolderController.class.getName());

    public FolderController() {
    }

    @RequestMapping(method = RequestMethod.POST, path="/create")
    public ResponseEntity<BaseResponse<Folder>> create(@RequestParam int ownerId,
                                                       @RequestParam int parentId, @RequestParam String title) {
        logger.info("in create()");

        if (title.equals("")) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Title cannot be empty!"));
        }

        logger.info("folder: " + title + " was successfully created");

        try {
            return ResponseEntity.ok(BaseResponse.success(folderService.createFolder(ownerId, parentId, title)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/setParent")
    public ResponseEntity<BaseResponse<Folder>> setParent(@RequestHeader int folderId, @RequestHeader int userId,
                                                          @RequestParam int parentId) {
        logger.info("in setParent()");

        try {
            return ResponseEntity.ok(BaseResponse.success(folderService.setParent(folderId, parentId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.PATCH, path="/setTitle")
    public ResponseEntity<BaseResponse<Folder>> setTitle(@RequestHeader int folderId, @RequestHeader int userId,
                                                         @RequestParam String title) {
        logger.info("in setTitle()");

        try {
            return ResponseEntity.ok(BaseResponse.success(folderService.setTitle(folderId, title)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int folderId) {
        logger.info("in delete()");

        if (folderService.delete(folderId)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "Folder was successfully deleted"));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Folder deletion failed"));
        }
    }
}
