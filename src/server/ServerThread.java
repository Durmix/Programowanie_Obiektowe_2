package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ServerThread {

    private static ServerThread server;

    private ServerSocket socket;

    private ExecutorService threads;

    private List<String> availableUsers;

    private final String[] discsPaths = {
            "D:\\ServerData\\Disc 1",
            "D:\\ServerData\\Disc 2",
            "D:\\ServerData\\Disc 3",
            "D:\\ServerData\\Disc 4",
            "D:\\ServerData\\Disc 5",
    };

    static ServerThread getServer() {
        if(server == null) {
            server = new ServerThread();
        }
        return server;
    }

    void init(int port, ExecutorService threads) throws IOException {
        server.socket = new ServerSocket(port);
        availableUsers = new ArrayList<>();

        this.threads = threads;
        threads.submit(this::clientsConnectionManager);
    }

    private void clientsConnectionManager() {
        while (true) {
            try {
                System.out.println("Connecting...");
                Socket check = socket.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(check.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(check.getInputStream());

                String userName = inputStream.readObject().toString();
                System.out.println("Connected with " + userName);
                availableUsers.add(userName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
