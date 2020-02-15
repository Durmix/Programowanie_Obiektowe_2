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

public class ClientController {

    public ClientController() {

    }

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Button sendButton;

    @FXML
    private Button closeButton;

    @FXML
    private Label currentStatus;

    @FXML
    private Label clientUserName;

    @FXML
    private Label localFolderPath;

    @FXML
    private ListView<String> localFiles;

    @FXML
    private ListView<String> activeUsers;

    @FXML
    protected void setClientUserNameLabel(String userName) {
        clientUserName.setText("Logged: " + userName);
    }

    @FXML
    protected void setLocalFolderPathLabel(String path) {
        localFolderPath.setText("Current directory: " + path);
    }

    @FXML
    protected void setCurrentStatusLabel(String status) {
        currentStatus.setText("Status: " + status);
    }

    @FXML
    protected void displayFiles(List<String> files) {
        ObservableList<String> items = observableArrayList();
        items.addAll(files);
        localFiles.setItems(items);
    }

    @FXML
    protected void displayUsers(List<String> loggedUsers) {
        ObservableList<String> users = observableArrayList();
        users.addAll(loggedUsers);
        activeUsers.setItems(users);
    }

    @FXML
    private void sendButtonAction() {
        ReadOnlyObjectProperty<String> file = localFiles.getSelectionModel().selectedItemProperty();
        String fileName = file.getValue();
        file = activeUsers.getSelectionModel().selectedItemProperty();
        String owner = file.getValue();

        System.out.println("Sending " + fileName + " to " + owner);
        ClientMain.clientThread.sendFile(fileName, owner);
    }

    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

}
