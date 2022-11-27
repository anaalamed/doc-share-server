package docSharing.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesUtils {
    public static String getFileName(String filePath){
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
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

    public static  void writeToFile(String content, String path){
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
