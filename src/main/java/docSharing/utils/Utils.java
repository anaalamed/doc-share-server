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

}
