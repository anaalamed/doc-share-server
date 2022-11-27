package docSharing.controller;

import docSharing.controller.request.UpdateRequest;
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

    // TODO: why join and leave are not REST calls? maybe update should be the only socket call?
    // and: check permissions
    @MessageMapping("/join/{documentId}")
    public void join(int documentId, int userId) {
        logger.info("in join()");

        documentService.join(documentId, userId);
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
//        documentService.update(id, updateRequest);
        return updateRequest;
    }
}