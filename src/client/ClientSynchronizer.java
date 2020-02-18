package client;

import javafx.application.Platform;
import utils.FileHolder;
import utils.Tools;

import java.io.*;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Class synchronize local folders with server
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class ClientSynchronizer {

    /**
     * Instance of ObjectOutputStream
     */
    private ObjectOutputStream outputStream;

    /**
     * Instance of ObjectOutputStream
     */
    private ObjectInputStream inputStream;

    /**
     * Thread pool
     */
    private ExecutorService threads;

    /**
     * Path to client's local folder
     */
    private String path;

    /**
     * List of files to send to other users
     */
    private List<FileHolder> filesToSend;

    /**
     * Username of a client
     */
    private String userName;

    /**
     * Constructor
     * @param inputStream input stream for client
     * @param outputStream output stream for client
     * @param path path to client's local folder
     * @param threads thread pool
     * @param filesToSend list of files to send to other users
     * @param userName client's username
     */
    protected ClientSynchronizer(ObjectInputStream inputStream, ObjectOutputStream outputStream, String path, ExecutorService threads, List<FileHolder> filesToSend, String userName) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.path = path;
        this.threads = threads;
        this.filesToSend = filesToSend;
        this.userName = userName;
    }

    /**
     * Method keeps server discs and local folders up to date to each other
     */
    protected void synchronize() {
        while(true) {
            try {
                Platform.runLater(() -> ClientMain.setCurrentStatus("Loading"));
                Thread.sleep(25);

                List<String> otherLoggedUsers = (List<String>) inputStream.readObject();
                otherLoggedUsers.remove(userName);
                ClientMain.controller.displayUsers(otherLoggedUsers);

                List<String> files = Tools.GetAllFilesInDirectory(path);
                outputStream.writeObject(files);

                List<String> newFilesToSend = (List<String>) inputStream.readObject();

                if (!filesToSend.isEmpty()) {
                    Platform.runLater(() -> ClientMain.setCurrentStatus("Sending " + filesToSend.get(0).getName() + " to " + filesToSend.get(0).getOwner()));
                    outputStream.writeObject(filesToSend.get(0));
                    filesToSend.remove(0);
                } else if (!newFilesToSend.isEmpty()) {
                    try {
                        Platform.runLater(() -> ClientMain.setCurrentStatus("File is being saved on the server"));
                        File file = new File(path + "\\" + newFilesToSend.get(0));
                        byte[] content = new byte[Math.toIntExact(file.length())];
                        FileInputStream fileInputStream = new FileInputStream(file);
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                        bufferedInputStream.read(content, 0, content.length);
                        FileHolder fileToSend = new FileHolder(userName, content, newFilesToSend.get(0));
                        outputStream.writeObject(fileToSend);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    outputStream.writeObject(null);
                }

                FileHolder receivedFile = (FileHolder) inputStream.readObject();

                if (receivedFile == null) {
                    continue;
                }

                Platform.runLater(() -> ClientMain.setCurrentStatus("Loading a file from the server"));

                if (receivedFile.getOwner().equals(userName)) {
                    threads.submit(() -> {
                       try(FileOutputStream fileOutputStream = new FileOutputStream(path + "\\" + receivedFile.getName())) {
                           fileOutputStream.write(receivedFile.getContent());
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                    });
                }
            } catch (InterruptedException e) {
                return;
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
