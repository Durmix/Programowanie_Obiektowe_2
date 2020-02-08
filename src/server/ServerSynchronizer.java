package server;

import utils.FileHolder;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class ServerSynchronizer {

    private ObjectOutputStream outputStream;

    private ObjectInputStream inputStream;

    private ExecutorService threads;

    private String[] discsPaths;

    private String userName;

    private Socket socket;

    private List<String> availableUsers;


    public ServerSynchronizer(ObjectOutputStream outputStream, ObjectInputStream inputStream, ExecutorService threads, String[] discsPaths, String userName, Socket socket, List<String> availableUsers) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.threads = threads;
        this.discsPaths = discsPaths;
        this.userName = userName;
        this.socket = socket;
        this.availableUsers = availableUsers;
    }

    void synchronizeLocalWithServer() {
        while (true) {
            try {
                System.out.println("List sent to " + userName + ": " + ServerMain.availableUsers);
                outputStream.writeObject(ServerMain.availableUsers);

                List<String> clientFiles = (List<String>) inputStream.readObject();

                List<String> serverFiles = utils.Tools.GetAllFilesInDirectory(discsPaths[0] + "\\" + userName);

                List<String> missingOnServer = clientFiles.stream()
                        .filter(i -> !serverFiles.contains(i))
                        .collect (Collectors.toList());

                List<String> missingOnClient = serverFiles.stream()
                        .filter(i -> !clientFiles.contains(i))
                        .collect (Collectors.toList());

                outputStream.writeObject(missingOnServer);

                FileHolder receivedFile = (FileHolder) inputStream.readObject();

                if(receivedFile != null) {
                    for(int i = 0; i < discsPaths.length; i++) {
                        int finalI = i;
                        threads.submit( () -> {
                            try(FileOutputStream fos = new FileOutputStream(discsPaths[finalI] + "\\" + receivedFile.getOwner() + "\\" + receivedFile.getName())) {
                                fos.write(receivedFile.getContent());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

                if(missingOnClient.isEmpty()) {
                    outputStream.writeObject(null);
                } else {
                    try {
                        File file = new File(discsPaths[0] + "\\" + userName + "\\" + missingOnClient.get(0));
                        byte[] content = new byte[(int)file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        bis.read(content, 0, content.length);

                        FileHolder sending = new FileHolder(userName, content, missingOnClient.get(0));

                        outputStream.writeObject(sending);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch(SocketException e) {
                try {
                    socket.close();
                } catch(Exception e1) {
                    e1.printStackTrace();
                }
                System.out.println("Connection dropped with: " + userName);
                availableUsers.remove(userName);
                System.out.println(availableUsers);
                //ServerMain.displayUsers(availableUsers);

                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
