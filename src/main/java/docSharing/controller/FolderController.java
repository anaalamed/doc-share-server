package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.entities.document.Folder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static docSharing.utils.Utils.*;

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
    public ResponseEntity<BaseResponse<Document>> create(@RequestHeader int id, @RequestParam User owner,
                                                         @RequestParam Folder parent, @RequestParam String title) {
        logger.info("in create");
        if(!isCreateValid(owner, title)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Create invalid!"));
        }
        return ResponseEntity.ok(BaseResponse.success(
                true,
                "folder: "+title+"created",
                folderService.createFolder(owner, parent, title, id)));
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/delete")
    public ResponseEntity<BaseResponse<Void>> delete(@RequestHeader int id, @RequestParam User user){
        logger.info("in delete");
        if(folderService.delete(id,user)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "folder deleted"));
        }
        else {
            return ResponseEntity.badRequest().body(BaseResponse.failure("The deletion failed"));
        }

    }


}
