package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final AtomicInteger clientCount = new AtomicInteger(0);

    public static void main(String[] args) {
        int port = 1234;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    int count = clientCount.incrementAndGet();
                    System.out.println("New client connected");

                    try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                        out.println(count); 
                    } catch (IOException e) {
                        System.out.println("Error sending message to client");
                        System.out.println(e.getMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + port);
            System.out.println(e.getMessage());
        }
    }
}
