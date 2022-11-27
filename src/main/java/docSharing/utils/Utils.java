package docSharing.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

public class Utils {
    public static String generateUniqueToken() {
        StringBuilder token = new StringBuilder();
        long currentTimeInMilisecond = Instant.now().toEpochMilli();

        return token.append(currentTimeInMilisecond).append("-")
                .append(UUID.randomUUID()).toString();
    }

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String passwordFromUser, String PasswordFromDB) {
        BCrypt.Result result = BCrypt.verifyer().verify(passwordFromUser.toCharArray(),
                PasswordFromDB.toCharArray());

        return result.verified;
    }

    public static String readFromFile(String path){
        String str = "";
        try {
            str = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
    public static String getFileName(String filePath){
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }

    public static void writeToFile(String content, String path){
        FileWriter fw;
        try {
            fw = new FileWriter(path);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
