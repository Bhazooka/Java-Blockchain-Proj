package receiver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import dbUtil.dbConnection;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReceiverController implements Initializable {

    @FXML private TextField id;
    @FXML private TextField firstname;
    @FXML private TextField lastname;
    @FXML private TextField email;
    @FXML private DatePicker dob;
    //TABLE DATA WINDOW
    @FXML private TableView<SenderData> studenttable;
    @FXML private TableColumn<SenderData, String> idcolumn;
    @FXML private TableColumn<SenderData, String> firstnamecolumn;
    @FXML private TableColumn<SenderData, String> lastnamecolumn;
    @FXML private TableColumn<SenderData, String> emailcolumn;
    @FXML private TableColumn<SenderData, String> dobcolumn;
    
    private dbConnection dc;
    private ObservableList<SenderData> data;
    
    private String sql = "SELECT id, fname, lname, email, DOB FROM students";
    
    public void initialize(URL url, ResourceBundle rb){
        this.dc = new dbConnection();
    }

    @FXML
    private void loadStudentData(ActionEvent event)throws SQLException{
        try {
            Connection conn = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                this.data.add(new SenderData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
        }catch (SQLException e){
            System.err.println("Error" + e);
        }

        this.idcolumn.setCellValueFactory(new PropertyValueFactory<SenderData, String>("ID"));
        this.firstnamecolumn.setCellValueFactory(new PropertyValueFactory<SenderData, String>("firstName"));
        this.lastnamecolumn.setCellValueFactory(new PropertyValueFactory<SenderData, String>("secondName"));
        this.emailcolumn.setCellValueFactory(new PropertyValueFactory<SenderData, String>("email"));
        this.dobcolumn.setCellValueFactory(new PropertyValueFactory<SenderData, String>("DOB"));

        this.studenttable.setItems(null);
        this.studenttable.setItems(this.data);
    }

    @FXML
    private void addStudent(ActionEvent event){
        String sqlInsert = "INSERT INTO students(id, fname, lname, email, DOB) VALUES (?,?,?,?,?)";

        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);

            stmt.setString(1, this.id.getText());
            stmt.setString(2, this.firstname.getText());
            stmt.setString(3, this.lastname.getText());
            stmt.setString(4, this.email.getText());
            stmt.setString(5, this.dob.getEditor().getText());

            stmt.execute();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFields(ActionEvent event){
        this.id.setText("");
        this.firstname.setText("");
        this.lastname.setText("");
        this.email.setText("");
        this.dob.setValue(null);
    }
    
    @FXML
    private void btnViewUser(ActionEvent event) {
    	
        SenderData selectedStudent = studenttable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            System.err.println("No sender selected.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("displayData.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            
            DisplayDataController displayDataController = loader.getController();
            
            displayDataController.setStudentDetails(selectedStudent.getID(), selectedStudent.getFirstName(), selectedStudent.getsecondName());
            displayDataController.loadCertificates(selectedStudent.getID(), selectedStudent.getFirstName(), selectedStudent.getsecondName());
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading the FXML file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
