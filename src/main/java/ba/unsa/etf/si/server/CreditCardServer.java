package ba.unsa.etf.si.server;

import ba.unsa.etf.si.utility.interfaces.MessageReceiver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;

public class CreditCardServer implements Runnable{

    private ServerSocket serverSocket;
    private BufferedReader inputStream;
    private MessageReceiver receiver;

    public CreditCardServer(int port, MessageReceiver receiver) {
        try {
            serverSocket = new ServerSocket(port);
            this.receiver = receiver;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        Socket socket = null;
        System.out.println("Opening connection");
        try {
            serverSocket.setSoTimeout(10 * 1000);
            socket = serverSocket.accept();
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            receiver.onMessageReceived(inputStream.readLine());
        }
        catch (SocketTimeoutException ignore) {

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
