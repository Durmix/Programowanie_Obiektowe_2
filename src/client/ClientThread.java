package client;

import utils.FileHolder;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ClientThread {

    private String path;

    private List<FileHolder> filesToSend;

    public ClientThread(String ip, int port, String path, String userName, ExecutorService threads) throws IOException {
        Socket socket = new Socket(ip, port);
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(userName);
        this.path = path;
        filesToSend = new ArrayList<>();
        ClientSynchronizer synchronizer = new ClientSynchronizer(inputStream, outputStream, threads, filesToSend, userName);
        threads.submit(synchronizer::synchronize);
    }

    protected void sendFile(String fileName, String recipient) {
        if (fileName == null || "".equals(recipient)) {
            //
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
