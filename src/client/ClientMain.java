package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class for JavaFX Client Application initialization
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class ClientMain extends Application {

    /**
     * Instance of ClientThread
     */
    protected static ClientThread clientThread;

    /**
     * Client's username
     */
    private String userName;

    /**
     * Path to client's local folder
     */
    private String path;

    /**
     * Thread pool
     */
    private ExecutorService threads;

    /**
     * Instance of ClientController
     * @see ClientController
     */
    protected static ClientController controller;

    /**
     * Path to CSS file from which Client Application gets its stylesheet
     */
    private File styleFile = new File("C:\\Users\\Durmaje\\IdeaProjects\\JavaProject\\src\\client\\Style.css");

    /**
     * Method sets label on synchronization status
     * @param status status to be displayed
     */
    static public void setCurrentStatus(String status) {
        controller.setCurrentStatusLabel(status);
    }

    /**
     * Method starts JavaFX Application
     * @param mainPage Main stage for Application
     */
    @Override
    public void start(Stage mainPage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientView.fxml"));
        Parent main = null;
        try {
            main = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = loader.getController();

        controller.displayFiles(Tools.GetAllFilesInDirectory(path));
        controller.setClientUserNameLabel(userName);
        controller.setLocalFolderPathLabel(path);

        mainPage.setTitle("CLIENT");
        mainPage.setResizable(false);
        Scene mainScene = new Scene(main, 480, 400);
        mainPage.setScene(mainScene);
        String style = getStyleFromFile(styleFile);
        mainScene.getStylesheets().add(style);
        mainPage.show();

        clientThread = new ClientThread("127.0.0.1", 1337, path, userName, threads);
        threads.submit(this::directoryManager);
    }

    /**
     * Method shutdowns Client Application
     */
    @Override
    public void stop() {
        threads.shutdown();
        threads.shutdownNow();
        System.out.println("See you soon");
        System.exit(0);
    }

    /**
     * Method sets parameters and creates thread pool
     */
    @Override
    public void init() {
        Parameters parameters = getParameters();
        List<String> params = parameters.getUnnamed();
        userName = params.get(0);
        path = params.get(1);
        threads = Executors.newFixedThreadPool(5);
    }

    /**
     * Method searches for any changes in client's folders and update files' view when any appears
     */
    private void directoryManager() {
        Path directory = Paths.get(path);
        try {
            WatchService watchService = directory.getFileSystem().newWatchService();
            WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                Thread.sleep(25);
                List<WatchEvent<?>> eventList = watchKey.pollEvents();
                for (WatchEvent event : eventList) {
                    if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE) || event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                        List<String> files = Tools.GetAllFilesInDirectory(path);
                        Platform.runLater(() -> controller.displayFiles(files));
                        break;
                    }
                }
            }
        } catch(InterruptedException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method converts CSS file to string
     * @param style CSS style file
     * @return String with path to style file
     */
    private String getStyleFromFile(File style) {
        try {
            return style.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Main method to launch Client Application
     * @param args userName and path to his local folder
     */
    public static void main(String[] args) {
        launch(args);
    }

}
