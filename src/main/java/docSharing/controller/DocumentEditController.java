package docSharing.controller;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.permission.Permission;
import docSharing.service.DocumentService;
import docSharing.service.PermissionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@ComponentScan
public class DocumentEditController {
    @Autowired
    private DocumentService documentService;
    @Autowired
    private PermissionService permissionService;

    private static final Logger logger = LogManager.getLogger(DocumentEditController.class.getName());

    public DocumentEditController() {
    }

    // TODO: why join and leave are not REST calls? maybe update should be the only socket call?
    // and: check permissions
    @MessageMapping("/join/{documentId}")
    @SendTo("/topic/join")
    public boolean join(int documentId, int userId) {
        logger.info("in join()");

        if(!permissionService.isAuthorized(documentId, userId, Permission.VIEWER)) {
            logger.warn("user is not authorized");
            return false;
        }

        documentService.join(documentId, userId);
        return true;
    }

    @MessageMapping("/leave") //TODO: add path 'leave' in client
    public void leave(int documentId, int userId) {
        logger.info("in leave()");

        documentService.leave(documentId, userId);
    }

    @MessageMapping("/update")
    @SendTo("/topic/updates")
    public UpdateRequest update(UpdateRequest updateRequest){
        logger.info("in update()");
        logger.info("update message:" + updateRequest.getContent());
        documentService.update(updateRequest);
        return updateRequest;
    }

    @MessageMapping("/hello")
    public void greet(String name){
        System.out.println("on connection name: "+name);
    }
}