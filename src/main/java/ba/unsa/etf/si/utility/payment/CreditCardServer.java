package ba.unsa.etf.si.utility.payment;

import ba.unsa.etf.si.interfaces.MessageReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class CreditCardServer implements Runnable{

    private ServerSocket serverSocket;
    private BufferedReader inputStream = null;
    private Socket socket = null;
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
        System.out.println("Opening connection");
        try {
            serverSocket.setSoTimeout(10 * 1000);
            socket = serverSocket.accept();
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = "";
            StringBuilder builder = new StringBuilder();
            while (!(line = inputStream.readLine()).equals("end")) builder.append(line);
            System.out.println(builder.toString());
            receiver.onMessageReceived(builder.toString());
        }
        catch (SocketTimeoutException ignore) {
            receiver.onMessageReceived("{\"error\": \"error\"}");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(inputStream != null) inputStream.close();
                if(socket != null) socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Closing connection");
    }
}
