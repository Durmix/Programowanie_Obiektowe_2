package server;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import utils.Tools;


import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * JavaFX controller for server application
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class ServerController {

    /**
     * Disc from which files are displayed
     */
    private String activeDisc = "Disc 1";

    /**
     * Path where server discs are saved
     */
    private String basePath = "D:\\ServerData\\";

    /**
     * Constructor
     */
    public ServerController() {

    }

    /**
     * List of users who have logged in to server at least once
     */
    @FXML
    private ListView<String> activeUsers;

    /**
     * List of files of the current user
     */
    @FXML
    private ListView<String> userFiles;

    /**
     * Menu to switch server disc
     */
    @FXML
    private MenuButton discSwitchButton;

    /**
     * Button which shutdown server application
     */
    @FXML
    Button closeButton;

    /**
     * Method picks disc from which files are displayed
     */
    @FXML
    private void pickDiscOne() {
        activeDisc = "Disc 1";
        discSwitchButton.setText("Disc 1");
        displayFilesFromDisc();
    }

    /**
     * Method picks disc from which files are displayed
     */
    @FXML
    private void pickDiscTwo() {
        activeDisc = "Disc 2";
        discSwitchButton.setText("Disc 2");
        displayFilesFromDisc();
    }

    /**
     * Method picks disc from which files are displayed
     */
    @FXML
    private void pickDiscThree() {
        activeDisc = "Disc 3";
        discSwitchButton.setText("Disc 3");
        displayFilesFromDisc();
    }

    /**
     * Method picks disc from which files are displayed
     */
    @FXML
    private void pickDiscFour() {
        activeDisc = "Disc 4";
        discSwitchButton.setText("Disc 4");
        displayFilesFromDisc();
    }

    /**
     * Method picks disc from which files are displayed
     */
    @FXML
    private void pickDiscFive() {
        activeDisc = "Disc 5";
        discSwitchButton.setText("Disc 5");
        displayFilesFromDisc();
    }

    /**
     * Method displays files of current user and directory
     */
    @FXML
    private void displayFilesFromDisc() {
        ReadOnlyObjectProperty<String> userModel = activeUsers.getSelectionModel().selectedItemProperty();
        String user = userModel.getValue();
        if (user == null) {
            return;
        }
        ObservableList<String> files = observableArrayList();
        files.addAll(Tools.GetAllFilesInDirectory(basePath + activeDisc + "\\" + user + "\\"));
        userFiles.setItems(files);
        System.out.println(basePath + activeDisc + "\\" + user + "\\");
    }

    /**
     * Method displays users who have logged in to the server at least once
     * @param loggedUsers List of users
     */
    @FXML
    protected void displayUsers(List<String> loggedUsers) {
        ObservableList<String> users = observableArrayList();
        users.addAll(loggedUsers);
        activeUsers.setItems(users);
    }

    /**
     * Method shutdowns server
     */
    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

}
