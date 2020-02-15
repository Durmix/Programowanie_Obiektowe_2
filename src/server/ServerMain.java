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

public class ServerMain extends Application {

    private final String[] discsPaths = {
            "D:\\ServerData\\Disc 1",
            "D:\\ServerData\\Disc 2",
            "D:\\ServerData\\Disc 3",
            "D:\\ServerData\\Disc 4",
            "D:\\ServerData\\Disc 5",
    };

    private static ServerThread server = ServerThread.getServer();

    private static ServerController controller;

    private ExecutorService threads;

    protected static List<String> availableUsers;

    private File styleFile = new File("C:\\Users\\Durmaje\\IdeaProjects\\JavaProject\\src\\server\\Style.css");

    @Override
    public void start(Stage mainPage) throws Exception {
        threads = Executors.newFixedThreadPool(20);

        FXMLLoader loader = new FXMLLoader();
        Parent main = loader.load(getClass().getResourceAsStream("ServerLayout.fxml"));
        controller = loader.getController();

        mainPage.setTitle("SERVER");
        mainPage.setResizable(false);
        Scene mainScene = new Scene(main, 450, 400);
        mainPage.setScene(mainScene);
        String style = getStyleFromFile(styleFile);
        mainScene.getStylesheets().add(style);
        mainPage.show();

        availableUsers = Tools.GetAllFilesInDirectory(discsPaths[0]);
        controller.displayUsers(availableUsers);
        threads.submit(this::directoryManager);
        server.init(1337, threads);
    }

    @Override
    public void stop() {
        threads.shutdown();
        threads.shutdownNow();
        System.out.println("Server disconnected");
        System.exit(0);
    }

    private String getStyleFromFile(File style) {
        try {
            return style.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

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
                        availableUsers = Tools.GetAllFilesInDirectory(discsPaths[0]);
                        Platform.runLater(() -> controller.displayUsers(availableUsers));
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
