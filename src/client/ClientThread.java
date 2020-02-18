package client;

import utils.FileHolder;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Class connects with server and starts synchronization
 * @author Kacper Durmaj (215712@edu.p.lodz.pl)
 */
public class ClientThread {

    /**
     * Path to user's local folder
     */
    private String path;

    /**
     * List of files to send to other users
     */
    private List<FileHolder> filesToSend;

    /**
     * Constructor
     * @param ip ip of a server
     * @param port port on which server works
     * @param path path to user's local folder
     * @param userName client's username
     * @param threads thread pool
     */
    protected ClientThread(String ip, int port, String path, String userName, ExecutorService threads) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.path = path;
        filesToSend = new ArrayList<>();
        ClientSynchronizer synchronizer = new ClientSynchronizer(inputStream, outputStream, path, threads, filesToSend, userName);
        threads.submit(synchronizer::synchronize);
    }

    /**
     * Method wraps a file into FileHolder and adds it to a list of files to send to server
     * @param fileName name of a file to send
     * @param recipient owner of this file
     */
    protected void sendFile(String fileName, String recipient) {
        if (fileName == null || "".equals(recipient)) {
            return;
        }
        try {
            File file = new File(path + "\\" + fileName);
            byte[] content = new byte[(int) file.length()];

            FileInputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            bufferedInputStream.read(content, 0, content.length);
            FileHolder fileToSend = new FileHolder(recipient, content, fileName);
            filesToSend.add(fileToSend);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
