package docSharing.controller;

import docSharing.controller.request.UpdateRequest;
import docSharing.entities.User;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import static docSharing.utils.Utils.*;


@Controller
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    private static final Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public DocumentController() {
    }

    @MessageMapping("/join")
    public void join(User user,String url){
        logger.info("in join");
        if(!isValidURL(url)){
            logger.error("Invalid URL!");
        }
        documentService.join(user,url);
        logger.info("user"+ user.getName() +"join to:"+ url);
    }

    @MessageMapping("/leave") //TODO: add path 'leave' in client
    public void leave(User user, String url) {
        logger.info("in leave");
        if(!isValidURL(url)){
            logger.error("Invalid URL!");
        }
        documentService.leave(user,url);
        logger.info("user"+ user.getName() +"leave:"+ url);
    }

    @MessageMapping("/update")
    @SendTo("/topic/updates")
    public void update(String url, UpdateRequest updateRequest){
        logger.info("in update");
        if(!isValidURL(url)){
            logger.error("Invalid URL!");
        }
        documentService.update(url, updateRequest);
        logger.info("update message:"+updateRequest.getContent()+"in url:"+url);
    }
}