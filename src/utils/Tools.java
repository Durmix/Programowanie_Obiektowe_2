package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class made to extract the particular method for more comfortable usage
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class Tools {

    /**
     * method provides a list of all files in given directory
     * @param directoryPath path of a folder from which it lists files
     * @return list of files in given folder
     */
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
