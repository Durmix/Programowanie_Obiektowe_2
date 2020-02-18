package server;

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
 * Main class for JavaFX Server Application initialization
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class ServerMain extends Application {

    /**
     * Paths of five server discs where data is stored
     */
    private final String[] discsPaths = {
            "D:\\ServerData\\Disc 1",
            "D:\\ServerData\\Disc 2",
            "D:\\ServerData\\Disc 3",
            "D:\\ServerData\\Disc 4",
            "D:\\ServerData\\Disc 5",
    };

    /**
     * Instance of ServerThread server
     * @see ServerThread
     */
    private static ServerThread server = ServerThread.getServer();

    /**
     * Instance of ServerController
     * @see ServerController
     */
    private static ServerController controller;

    /**
     * Thread pool
     */
    private ExecutorService threads;

    /**
     * List of users who have logged in at least once
     */
    protected static List<String> activeUsers;

    /**
     * Path to CSS file from which Server Application gets its stylesheet
     */
    private File styleFile = new File("C:\\Users\\Durmaje\\IdeaProjects\\JavaProject\\src\\server\\Style.css");

    /**
     * Method starts JavaFX Application
     * @param mainPage Main stage for Application
     */
    @Override
    public void start(Stage mainPage) {
        threads = Executors.newFixedThreadPool(20);

        FXMLLoader loader = new FXMLLoader();
        Parent main = null;
        try {
            main = loader.load(getClass().getResourceAsStream("ServerLayout.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = loader.getController();

        mainPage.setTitle("SERVER");
        mainPage.setResizable(false);
        Scene mainScene = new Scene(main, 450, 400);
        mainPage.setScene(mainScene);
        String style = getStyleFromFile(styleFile);
        mainScene.getStylesheets().add(style);
        mainPage.show();

        activeUsers = Tools.GetAllFilesInDirectory(discsPaths[0]);
        controller.displayUsers(activeUsers);
        threads.submit(this::directoryManager);
        server.init(1337, threads);
    }

    /**
     * Shutdowns server and all tasks
     */
    @Override
    public void stop() {
        threads.shutdown();
        threads.shutdownNow();
        System.out.println("Server disconnected");
        System.exit(0);
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
     * Method manages clients' folders and display logged users
     */
    private void directoryManager() {
        Path directory = Paths.get(discsPaths[0]);
        try {
            WatchService watchService = directory.getFileSystem().newWatchService();
            WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            while(true) {
                Thread.sleep(25);
                List<WatchEvent<?>> eventList = watchKey.pollEvents();
                for (WatchEvent event : eventList) {
                    if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE) || event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                        activeUsers = Tools.GetAllFilesInDirectory(discsPaths[0]);
                        Platform.runLater(() -> controller.displayUsers(activeUsers));
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
