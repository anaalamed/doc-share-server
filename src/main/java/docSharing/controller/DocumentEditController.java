package docSharing.controller;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.User;
import docSharing.entities.document.Document;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import static docSharing.utils.Utils.*;

@Controller
@ComponentScan
public class DocumentEditController {
    @Autowired
    private DocumentService documentService;//check if needed?

    private static final Logger logger = LogManager.getLogger(DocumentEditController.class.getName());

    public DocumentEditController() {
    }

    @MessageMapping("/join")
    public boolean join(int id, User user){
        logger.info("in join");
        if(documentService.join(id, user)) {
            logger.info("user" + user.getName() + "join");
            return true;
        }else{
            logger.error("user" + user.getName() + "failed to join");
            return false;
        }
    }

    @MessageMapping("/leave") //TODO: add path 'leave' in client
    public boolean leave(int id, User user){
        logger.info("in leave");
        if(documentService.leave(id, user)) {
            logger.info("user" + user.getName() + "leave");
            return true;
        }
        else{
            logger.error("user" + user.getName() + "failed to leave");
            return false;
        }
    }

    @MessageMapping("/update")
    @SendTo("/topic/updates")
    public Document update(int id, UpdateRequest updateRequest){
        logger.info("in update");
        logger.info("update message:"+updateRequest.getContent());
        return documentService.update(id, updateRequest);
    }
}