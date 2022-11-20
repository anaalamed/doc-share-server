package docSharing.utils;

import docSharing.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class.getName());

    public static String generateUniqueToken() {
        StringBuilder token = new StringBuilder();
        long currentTimeInMilisecond = Instant.now().toEpochMilli();

        return token.append(currentTimeInMilisecond).append("-")
                .append(UUID.randomUUID().toString()).toString();
    }

    public static boolean isUserNull (User user){
        return user.getId() == 0;
    }
    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
    public static boolean isCreateValid(User owner, String title) {
        if (isUserNull(owner)) {
            logger.error("NULL user trying to create file!");
            return false;
        }
        if (title.equals("")) {
            logger.info("The document will be created without a title");
            return false;
        }
        return true;
    }
}
