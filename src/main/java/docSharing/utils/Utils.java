package docSharing.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.time.Instant;
import java.util.UUID;

public class Utils {

    public static String generateUniqueToken() {
        StringBuilder token = new StringBuilder();
        long currentTimeInMilisecond = Instant.now().toEpochMilli();

        return token.append(currentTimeInMilisecond).append("-")
                .append(UUID.randomUUID().toString()).toString();
    }

    public static String hashPassword(String password){
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
