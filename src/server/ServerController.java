package server;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ListView;


import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class ServerController {

    private String activeDisc = "Disc 1";

    private String basePath = "D:\\ServerData\\";

    public ServerController() {

    }

    @FXML
    private ListView<String> activeUsers;

    @FXML
    private ListView<String> userFiles;

    @FXML
    private MenuButton discSwitchButton;

    @FXML
    private void pickDiscOne() {
        activeDisc = "Disc 1";
        discSwitchButton.setText("Disc 1");
        displayFilesFromDisc();
    }

    @FXML
    private void pickDiscTwo() {
        activeDisc = "Disc 2";
        discSwitchButton.setText("Disc 2");
        displayFilesFromDisc();
    }

    @FXML
    private void pickDiscThree() {
        activeDisc = "Disc 3";
        discSwitchButton.setText("Disc 3");
        displayFilesFromDisc();
    }

    @FXML
    private void pickDiscFour() {
        activeDisc = "Disc 4";
        discSwitchButton.setText("Disc 4");
        displayFilesFromDisc();
    }

    @FXML
    private void pickDiscFive() {
        activeDisc = "Disc 5";
        discSwitchButton.setText("Disc 5");
        displayFilesFromDisc();
    }

    @FXML
    private void displayFilesFromDisc() {
        ReadOnlyObjectProperty<String> userModel = activeUsers.getSelectionModel().selectedItemProperty();
        String user = userModel.getValue();
        if (user == null) {
            return;
        }
        ObservableList<String> files = observableArrayList();
        files.addAll(utils.Tools.GetAllFilesInDirectory(basePath + activeDisc + "\\" + user + "\\"));
        userFiles.setItems(files);
        System.out.println(basePath + activeDisc + "\\" + user + "\\");
    }

    @FXML
    private void displayUsers(List<String> loggedUsers) {
        ObservableList<String> users = observableArrayList();
        users.addAll(loggedUsers);
        activeUsers.setItems(users);
    }

}
