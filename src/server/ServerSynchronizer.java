package server;

import utils.FileHolder;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Class synchronize server with local folders
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class ServerSynchronizer {

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
     * Paths to server discs
     */
    private String[] discsPaths;

    /**
     * User for whom the synchronizer was created
     */
    private String userName;

    /**
     * Socket used for connection between server and client
     */
    private Socket socket;

    /**
     * List of logged users
     */
    private List<String> activeUsers;

    /**
     * Constructor for ServerSynchronizer
     * @param outputStream output stream for server
     * @param inputStream input stream for server
     * @param threads thread pool
     * @param discsPaths paths to server discs
     * @param userName client for whom it is being created
     * @param socket connecting server with client
     * @param activeUsers list of logged users
     */
    public ServerSynchronizer(ObjectOutputStream outputStream, ObjectInputStream inputStream, ExecutorService threads, String[] discsPaths, String userName, Socket socket, List<String> activeUsers) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.threads = threads;
        this.discsPaths = discsPaths;
        this.userName = userName;
        this.socket = socket;
        this.activeUsers = activeUsers;
    }

    /**
     * Method keeps server discs and local folders up to date to each other
     */
    protected void synchronizeLocalWithServer() {
        while (true) {
            try {
                System.out.println("List sent to " + userName + ": " + ServerMain.activeUsers);
                outputStream.writeObject(ServerMain.activeUsers);

                List<String> clientFiles = (List<String>) inputStream.readObject();

                List<String> serverFiles = utils.Tools.GetAllFilesInDirectory(discsPaths[0] + "\\" + userName);

                List<String> missingOnServer = clientFiles.stream()
                        .filter(i -> !serverFiles.contains(i))
                        .collect(Collectors.toList());

                List<String> missingOnClient = serverFiles.stream()
                        .filter(i -> !clientFiles.contains(i))
                        .collect(Collectors.toList());

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
                activeUsers.remove(userName);
                System.out.println(activeUsers);

                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
