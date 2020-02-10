package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Tools;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMain extends Application {

    protected static ClientThread clientThread;

    private String userName;

    private String path;

    private ExecutorService threads;

    protected static ClientController controller;

    @Override
    public void start(Stage mainStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent main = loader.load(getClass().getResourceAsStream("ClientView.fxml"));
        controller = loader.getController();

        controller.displayFiles(Tools.GetAllFilesInDirectory(path));
        controller.setClientUserNameLabel(userName);
        controller.setLocalFolderPathLabel(path);

        mainStage.setTitle("CLIENT");
        Scene scene = new Scene(main, 480, 400);
        mainStage.setScene(scene);
        scene.getStylesheets().add(ClientMain.class.getResource("style.css").toExternalForm());
        mainStage.setResizable(false);
        mainStage.show();

        clientThread = new ClientThread("127.0.0.1", 1337, path, userName, threads);
        threads.submit(this::directoryManager);
    }

    @Override
    public void stop() {
        threads.shutdown();
        threads.shutdownNow();
        System.out.println("See you soon");
        System.exit(0);
    }

    @Override
    public void init() {
        Parameters parameters = getParameters();
        List<String> params = parameters.getUnnamed();
        userName = params.get(0);
        threads = Executors.newFixedThreadPool(5);
    }

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

    static public void setCurrentStatus(String status) {
        controller.setCurrentStatusLabel(status);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
