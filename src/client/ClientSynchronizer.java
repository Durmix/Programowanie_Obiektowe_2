package client;

import javafx.application.Platform;
import utils.FileHolder;
import utils.Tools;

import java.io.*;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ClientSynchronizer {

    private ObjectOutputStream outputStream;

    private ObjectInputStream inputStream;

    private ExecutorService threads;

    private String path;

    private List<FileHolder> filesToSend;

    private String userName;

    protected ClientSynchronizer(ObjectInputStream inputStream, ObjectOutputStream outputStream, String path, ExecutorService threads, List<FileHolder> filesToSend, String userName) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.path = path;
        this.threads = threads;
        this.filesToSend = filesToSend;
        this.userName = userName;
    }

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
