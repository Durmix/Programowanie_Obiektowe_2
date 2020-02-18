package server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Class creates directories on server discs and synchronizer for each client
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class ServerThread {

    /**
     * Instance of server
     */
    private static ServerThread server;

    /**
     * ServerSocket of server
     */
    private ServerSocket socket;

    /**
     * Thread pool
     */
    private ExecutorService threads;

    /**
     * List of logged users
     */
    private List<String> activeUsers;

    /**
     * Paths to server discs
     */
    private final String[] discsPaths = {
            "D:\\ServerData\\Disc 1",
            "D:\\ServerData\\Disc 2",
            "D:\\ServerData\\Disc 3",
            "D:\\ServerData\\Disc 4",
            "D:\\ServerData\\Disc 5",
    };

    /**
     * Method checks if there is an instance of server and if not creates one
     * @return active ServerThread instance
     */
    static ServerThread getServer() {
        if(server == null) {
            server = new ServerThread();
        }
        return server;
    }

    /**
     * Method initializes ServerSocket and thread for ConnectionManager
     * @param port port on which server will work
     * @param threads thread pool
     */
    void init(int port, ExecutorService threads) {
        try {
            server.socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        activeUsers = new ArrayList<>();

        this.threads = threads;
        threads.submit(this::connectionManager);
    }

    /**
     * Method searches for new clients, create directories for them on server discs and initialize new ServerSynchronizer for each user
     */
    private void connectionManager() {
        while (true) {
            try {
                System.out.println("Connecting...");
                Socket connecting = socket.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(connecting.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(connecting.getInputStream());

                String userName = inputStream.readObject().toString();
                System.out.println("Connected with " + userName);
                activeUsers.add(userName);
                for (String path : discsPaths) {
                    File directory = new File(path + "\\" + userName);
                    if (!directory.exists() || !directory.isDirectory()) {
                        directory.mkdir();
                    }
                }
                ServerSynchronizer synchronizer = new ServerSynchronizer(outputStream, inputStream, threads, discsPaths, userName, connecting, activeUsers);
                threads.submit(synchronizer::synchronizeLocalWithServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
