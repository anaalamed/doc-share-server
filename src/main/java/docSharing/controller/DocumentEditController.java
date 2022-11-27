package docSharing.controller;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.document.Document;
import docSharing.service.DocumentService;
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
    private DocumentService documentService; //check if needed?

    private static final Logger logger = LogManager.getLogger(DocumentEditController.class.getName());

    public DocumentEditController() {
    }

    @MessageMapping("/join")
    public boolean join(int id, int userId) {
        logger.info("in join()");

        if (documentService.join(id, userId)) {
            logger.info("user" + userId + "join");
            return true;
        } else {
            logger.error("user" + userId + "failed to join");
            return false;
        }
    }

    @MessageMapping("/leave") //TODO: add path 'leave' in client
    public boolean leave(int id, int userId) {
        logger.info("in leave()");
        if (documentService.leave(id, userId)) {
            logger.info("user" + userId + "leave");
            return true;
        } else {
            logger.error("user" + userId + "failed to leave");
            return false;
        }
    }

    @MessageMapping("/update")
    @SendTo("/topic/updates")
    public UpdateRequest update(UpdateRequest updateRequest){
        logger.info("in update()");
        logger.info("update message:" + updateRequest.getContent());
//        documentService.update(id, updateRequest);
        return updateRequest;
    }

    @MessageMapping("/import")//TODO: implement path on client
    @SendTo("/topic/import")
    public Document importFile(String filePath, int ownerId, int parentID) {
        return documentService.importFile(filePath,ownerId,parentID);
    }

    @MessageMapping("/export")//TODO: implement path on client
    @SendTo("/topic/export")
    public void exportFile(int documentId) {
        documentService.exportFile(documentId);
    }

    @MessageMapping("/hello")
    public void greet(String name){
        System.out.println("on connection name: "+name);
    }
}