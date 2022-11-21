//package docSharing.controller;
//
//import docSharing.controller.request.UpdateRequest;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
////example!!!!
//
//@Controller
//public class chatController {
//    private static Logger logger = LogManager.getLogger(chatController.class.getName());
//    @MessageMapping("/join")
//    public void sendPlainMessage(JoinMessage message) {
//        System.out.println(message.user + " joined");
//    }
//
//
//    @MessageMapping("/update")
//    @SendTo("/topic/updates")
//    public UpdateRequest sendPlainMessage(UpdateRequest message) {
//        logger.info("in sendPlainMessage");
//        return message;
//    }
//
//
//    private class JoinMessage {
//        private String user;
//
//        public JoinMessage() {
//        }
//
//        public String getUser() {
//            return user;
//        }
//
//        public void setUser(String user) {
//            this.user = user;
//        }
//    }
//}