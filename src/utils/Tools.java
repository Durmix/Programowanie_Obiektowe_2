package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tools {

    public static List<String> GetAllFilesInDirectory(String directoryPath) {
        if (directoryPath == null) {
            return null;
        }

        File folder = new File(directoryPath);
        List<String> listOfFiles = new ArrayList<>();

        if (folder.exists()) {
            Collections.addAll(listOfFiles, folder.list());
        } else {
            listOfFiles.add("Directory not found");
        }

        return listOfFiles;
    }

}
