package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class LoginApp extends Application {
    private static final String SERVER_ADDRESS = "localhost"; 
    private static final int SERVER_PORT = 1234; 

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("School Management System");
        stage.show();
    }

    public static void main(String[] args) {
        int numberOfClients = connectToServer();
        if (numberOfClients > 0) {
            System.out.println("Number of clients connected: " + numberOfClients);
            launch(args);
        } else {
            System.err.println("Could not connect to server.");
            System.exit(1); 
        }
    }

    private static int connectToServer() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String response = reader.readLine();
            int count = Integer.parseInt(response);
            System.out.println("Connected to server. Clients connected: " + count);
            return count;
        } catch (IOException e) {
            System.err.println("Unable to connect to server: " + e.getMessage());
            return 0;
        }
    }
}
