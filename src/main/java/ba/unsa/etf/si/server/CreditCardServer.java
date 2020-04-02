package ba.unsa.etf.si.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class CreditCardServer implements Runnable{

    private ServerSocket serverSocket;
    private BufferedReader inputStream;

    public CreditCardServer(int port) {
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        Socket socket = null;
        System.out.println("Opening connection");
        try {
            socket = serverSocket.accept();
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String utf = "";
            do {
                utf = inputStream.readLine();
                System.out.println(utf);
            }
            while (!utf.equals("bye"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Closing connection");
        try {
            inputStream.close();
            Objects.requireNonNull(socket).close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
