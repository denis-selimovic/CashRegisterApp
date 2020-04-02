package ba.unsa.etf.si.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CreditCardServer implements Runnable{

    private ServerSocket serverSocket;
    private DataInputStream inputStream;

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
        try {
            socket = serverSocket.accept();
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            String utf = "";
            while (!(utf = inputStream.readUTF()).equals("over")) {
                System.out.println(utf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
