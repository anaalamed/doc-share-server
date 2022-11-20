package docSharing.controller;

import docSharing.controller.request.UpdateRequest;
import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.service.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static docSharing.utils.Utils.isValidURL;


@RestController
@CrossOrigin
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    private static final Logger logger = LogManager.getLogger(DocumentController.class.getName());

    public DocumentController() {
    }

    @RequestMapping(method = RequestMethod.POST, path= "/join")
    public ResponseEntity<BaseResponse<String>> join(@RequestParam User user,@RequestParam String url){
        logger.info("in join");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        documentService.join(user,url);
        return ResponseEntity.ok(BaseResponse.success("user"+ user.getName() +"join to:"+ url));
    }

    @RequestMapping(method = RequestMethod.DELETE,path="/leave")
    public ResponseEntity<BaseResponse<String>> leave(@RequestParam User user,@RequestParam String url) {
        logger.info("in leave");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        documentService.leave(user,url);
        return ResponseEntity.ok(BaseResponse.success("user"+ user.getName() +"leave:"+ url));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/update/{updateMessage}", params="url")
    public  ResponseEntity<BaseResponse<String>> update(@RequestParam String url,@PathVariable("updateMessage") UpdateRequest updateRequest){
        logger.info("in update");
        if(!isValidURL(url)){
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid URL!"));
        }
        documentService.update(url, updateRequest);
        return ResponseEntity.ok(BaseResponse.success("update message:"+updateRequest.getContent()+"in url:"+url));
    }
}