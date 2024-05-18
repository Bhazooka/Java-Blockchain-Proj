package client;

import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import receiver.ReceiverController;
import receiver.SenderData;
import sender.SenderController;
import sender.SenderRegisterController;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    LoginModel loginModel = new LoginModel();
    @FXML
    private Label dbstatus;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private ComboBox<option> combobox;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginStatus;
    @FXML
    private ImageView img;

    public void initialize(URL url, ResourceBundle rb){
        if (this.loginModel.isDatabaseConnected()) {
            this.dbstatus.setText("Connected to Database");
        } else {
            this.dbstatus.setText("Not Connected To Database");
        }

        this.combobox.setItems(FXCollections.observableArrayList(option.values()));
    }

    @FXML
    public void Login(ActionEvent event) {
        try {
        	
            if (this.combobox.getValue() == null) {
                this.loginStatus.setText("Please select a role.");
                return;
            }
        	
            String selectedOption = ((option) this.combobox.getValue()).toString();
            boolean loginSuccess = false;

            if 
            ("Receiver".equals(selectedOption)) {
                loginSuccess = this.loginModel.isLoginAdmin(this.username.getText(), this.password.getText());
            } 
            else if 
            ("Sender".equals(selectedOption)) {
                loginSuccess = this.loginModel.isLoginStudent(this.username.getText(), this.password.getText());
            }

            if (loginSuccess) {
            	SenderData student = getStudentInfo(this.username.getText());
                Stage stage = (Stage) this.loginButton.getScene().getWindow();
                stage.close();

                if 
                ("Receiver".equals(selectedOption)) {
                    adminLogin();
                } 
                else if 
                ("Sender".equals(selectedOption)) {
                    if (student != null) {
                        Stage studentStage = (Stage) this.loginButton.getScene().getWindow();
                        studentStage.close();
                        studentLogin(student);
                    } else {
                        //this.loginStatus.setText("Unable to retrieve sender data.");
                    }
                    
                }
            } else {
                this.loginStatus.setText("Wrong Credentials");
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @FXML
    public void studentRegister() {
        try {
            Stage userStage = new Stage();
            FXMLLoader studentLoader = new FXMLLoader();
            Pane studentroot = (Pane) studentLoader.load(getClass().getResource("/sender/senderRegister.fxml").openStream());

            SenderRegisterController studentsController = (SenderRegisterController) studentLoader.getController();

            Scene scene = new Scene(studentroot);
            userStage.setScene(scene);
            userStage.setTitle("Student Register");
            userStage.setResizable(false);
            userStage.show();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    private SenderData getStudentInfo(String username) {
        String sql = "SELECT id, fname, lname, email, DOB FROM students WHERE fname = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SenderData(rs.getString("id"), rs.getString("fname"), rs.getString("lname"), rs.getString("email"), rs.getString("DOB"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void studentLogin(SenderData student) {
        try {
            Stage userStage = new Stage();
            FXMLLoader studentLoader = new FXMLLoader();
            Pane studentroot = (Pane) studentLoader.load(getClass().getResource("/sender/senderMain.fxml").openStream());

            SenderController senderController = studentLoader.getController();
            senderController.setStudentData(student.getID(), student.getFirstName(), student.getsecondName());

            Scene scene = new Scene(studentroot);
            userStage.setScene(scene);
            userStage.setTitle("Student Dashboard");
            userStage.setResizable(false);
            userStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void adminLogin() {
        try {
            Stage adminStage = new Stage();
            FXMLLoader adminLoader = new FXMLLoader();
            Pane adminroot = (Pane) adminLoader.load(getClass().getResource("../receiver/receiver.fxml").openStream());

            ReceiverController receiverController = (ReceiverController) adminLoader.getController();

            Scene scene = new Scene(adminroot);
            adminStage.setScene(scene);
            adminStage.setTitle("receiver Dashboard");
            adminStage.setResizable(false);
            adminStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
