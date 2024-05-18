package sender;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import dbUtil.dbConnection;

public class SenderRegisterController implements Initializable {

    @FXML private TextField id;
    @FXML private TextField firstname;
    @FXML private TextField lastname;
    @FXML private TextField email;
    @FXML private TextField password;
    @FXML private DatePicker dob;

    private dbConnection dc;

    public void initialize(URL url, ResourceBundle rb) {
        this.dc = new dbConnection();
    }

    @FXML
    private void addStudent(ActionEvent event) {
        // Validate input fields are not empty
        if (id.getText().trim().isEmpty() ||
            firstname.getText().trim().isEmpty() ||
            lastname.getText().trim().isEmpty() ||
            email.getText().trim().isEmpty() ||
            password.getText().trim().isEmpty() ||
            dob.getEditor().getText().trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields to register a sender.");
            alert.showAndWait();
            return; // Stop the execution if any field is empty
        }

        String sqlInsert = "INSERT INTO students(id, fname, lname, email, pword, DOB) VALUES (?,?,?,?,?,?)";
        String sqlInsertlogin = "INSERT INTO login(username, password, division) VALUES (?,?,'Receiver')";

        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmtLogin = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // Insert into students table
            stmt = conn.prepareStatement(sqlInsert);
            stmt.setString(1, this.id.getText());
            stmt.setString(2, this.firstname.getText());
            stmt.setString(3, this.lastname.getText());
            stmt.setString(4, this.email.getText());
            stmt.setString(5, this.password.getText());
            stmt.setString(6, this.dob.getEditor().getText());
            stmt.execute();

            // Insert into login table
            stmtLogin = conn.prepareStatement(sqlInsertlogin);
            stmtLogin.setString(1, this.firstname.getText());  // username is set to the firstname of the student
            stmtLogin.setString(2, this.password.getText());   // password is set to the password field
            stmtLogin.execute();

            conn.commit();  // Commit both inserts as a single transaction

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("User created successfully!");
            alert.showAndWait();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Roll back transaction if exception occurs
                } catch (SQLException ex) {
                    System.err.println("Failed to rollback transaction: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create sender: " + e.getMessage());
            alert.showAndWait();
        } finally {
            // Clean up database resources
            try {
                if (stmt != null) stmt.close();
                if (stmtLogin != null) stmtLogin.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error on closing database resources: " + ex.getMessage());
            }
        }
    }


    @FXML
    private void clearFields(ActionEvent event) {
        this.id.setText("");
        this.firstname.setText("");
        this.lastname.setText("");
        this.email.setText("");
        this.password.setText("");
        this.dob.setValue(null);
    }
    
}
