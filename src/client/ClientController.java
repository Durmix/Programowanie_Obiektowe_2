package client;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * JavaFX controller for client application
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class ClientController {

    /**
     * Constructor
     */
    public ClientController() {

    }

    /**
     * Main AnchorPane at client interface
     */
    @FXML
    AnchorPane anchorPane;

    /**
     * Button to file sending
     */
    @FXML
    private Button sendButton;

    /**
     * Button to application closing
     */
    @FXML
    private Button closeButton;

    /**
     * Label with status of synchronization with server
     */
    @FXML
    private Label currentStatus;

    /**
     * Label with client's username
     */
    @FXML
    private Label clientUserName;

    /**
     * Label with a path to the local folder
     */
    @FXML
    private Label localFolderPath;

    /**
     * List of files in user's local folder
     */
    @FXML
    private ListView<String> localFiles;

    /**
     * List of users connected to the server
     */
    @FXML
    private ListView<String> activeUsers;

    /**
     * Method sets logged username label
     * @param userName username of logged client
     */
    @FXML
    protected void setClientUserNameLabel(String userName) {
        clientUserName.setText("Logged: " + userName);
    }

    /**
     * Method sets client's local folder label
     * @param path path to client's local folder
     */
    @FXML
    protected void setLocalFolderPathLabel(String path) {
        localFolderPath.setText("Current directory: " + path);
    }

    /**
     * Method sets label on synchronization status
     * @param status status to be displayed
     */
    @FXML
    protected void setCurrentStatusLabel(String status) {
        currentStatus.setText("Status: " + status);
    }

    /**
     * Method displays files from client's local folder
     * @param files list of files in directory
     */
    @FXML
    protected void displayFiles(List<String> files) {
        ObservableList<String> items = observableArrayList();
        items.addAll(files);
        localFiles.setItems(items);
    }

    /**
     * Method displays available users
     * @param loggedUsers list of logged users
     */
    @FXML
    protected void displayUsers(List<String> loggedUsers) {
        ObservableList<String> users = observableArrayList();
        users.addAll(loggedUsers);
        activeUsers.setItems(users);
    }

    /**
     * Method initializes sendFile action for current user and picked file
     */
    @FXML
    private void sendButtonAction() {
        ReadOnlyObjectProperty<String> file = localFiles.getSelectionModel().selectedItemProperty();
        String fileName = file.getValue();
        file = activeUsers.getSelectionModel().selectedItemProperty();
        String owner = file.getValue();

        System.out.println("Sending " + fileName + " to " + owner);
        ClientMain.clientThread.sendFile(fileName, owner);
    }

    /**
     * Method shutdowns client application
     */
    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

}
