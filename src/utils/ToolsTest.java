package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ToolsTest {

    @Test
    public void GetAllFilesInDirectoryTestNullPath() {
        List<String> testList = Tools.GetAllFilesInDirectory(null);

        Assertions.assertNull(testList);
    }

    @Test
    public void GetAllFilesInDirectoryTestNotExistingDirectory() {
        List<String> testList = Tools.GetAllFilesInDirectory("Folder Which Doesn't Exists");
        List<String> secondTestList = new ArrayList<>();
        secondTestList.add("Directory not found");

        Assertions.assertEquals(testList, secondTestList);
    }

    @Test
    public void GetAllFilesInDirectoryTestExistingDirectory() {
        List<String> testList = Tools.GetAllFilesInDirectory("D:\\TestData\\TestFolder");
        List<String> secondTestList = new ArrayList<>();
        secondTestList.add("File1.txt");
        secondTestList.add("File2.txt");
        secondTestList.add("File3.txt");

        Assertions.assertEquals(testList, secondTestList);
    }

}