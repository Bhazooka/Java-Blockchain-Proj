package sender;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import client.LoginModel;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import receiver.SenderData;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class SenderController {
	
	@FXML private Label lblIDText;
    @FXML private Label lblNameText;
    @FXML private Label lblSurnameText;
    @FXML private TextField txtFileName;
    @FXML private AnchorPane uploadedPDF;
    @FXML private AnchorPane declinedPDF;
    @FXML private AnchorPane approvedPDF;
    
    private String studentID;
    private String studentFirstName;
    private String studentLastName;
    
    private java.io.File selectedFile;
    
    public void setStudentData(String id, String firstName, String lastName) {
        this.studentID = id;
        this.studentFirstName = firstName;
        this.studentLastName = lastName;

        lblIDText.setText(String.valueOf(studentID));
        lblNameText.setText(studentFirstName);
        lblSurnameText.setText(studentLastName);
    }
    
    @FXML
    public void btnAddCertificate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Certificate File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        Stage stage = new Stage();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            txtFileName.setText(selectedFile.getName());
            System.out.println("Selected File: " + selectedFile.getAbsolutePath());
        } else {
            txtFileName.setText("");
            System.out.println("No file was selected.");
        }
    }

    
    public void btnSubmitCertificate() {
        
        String studentID = lblIDText.getText();
        String studentFirstName = lblNameText.getText();
        String studentLastName = lblSurnameText.getText();

        if (studentID == null || studentID.isEmpty() || studentFirstName == null || studentFirstName.isEmpty() || studentLastName == null || studentLastName.isEmpty() || selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Submission Failed");
            alert.setHeaderText(null);
            alert.setContentText("All data must be filled in before submitting.");
            alert.showAndWait();
            return;
        }

        String directoryName = (!studentFirstName.isEmpty() && !studentLastName.isEmpty())
            ? studentFirstName + studentLastName : "NoNameFile";

        try {
            
            String dataToWrite = studentID + ", " + studentFirstName + ", " + studentLastName + ", " + selectedFile.getName();
            java.nio.file.Path dataPath = java.nio.file.Paths.get("data/certificates.txt");
            java.nio.file.Files.createDirectories(dataPath.getParent());
            java.nio.file.Files.write(dataPath, (dataToWrite + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);

            java.nio.file.Path destinationDirectory = java.nio.file.Paths.get("certificate/" + directoryName);
            java.nio.file.Files.createDirectories(destinationDirectory); // Create directory if it doesn't exist
            java.nio.file.Path destinationPath = destinationDirectory.resolve(selectedFile.getName());
            java.nio.file.Files.copy(selectedFile.toPath(), destinationPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Submission Successful");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Successfully submitted \"" + selectedFile.getName() + "\" file.");
            successAlert.showAndWait();

            System.out.println("Data submitted and file copied successfully.");
        } catch (IOException e) {
            System.out.println("Failed to write data to file or copy file: " + e.getMessage());

            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Submission Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Failed to write data or copy file: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }
    
    @FXML
    public void btnLogout() {
        try {
            URL url = getClass().getResource("/client/login.fxml");
            Parent loginView = FXMLLoader.load(url);

            Stage currentStage = (Stage) uploadedPDF.getScene().getWindow();

            Scene loginScene = new Scene(loginView);
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Loading Error");
            alert.setContentText("Unable to load the login screen: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    public void btnViewUploadedCertificates() {
        String directoryName = (!studentFirstName.isEmpty() && !studentLastName.isEmpty())
            ? studentFirstName + studentLastName : "NoNameFile";

        java.nio.file.Path directoryPath = java.nio.file.Paths.get("certificate/" + directoryName);

        try {
            if (java.nio.file.Files.exists(directoryPath)) {
                StringBuilder fileNames = new StringBuilder();
                java.nio.file.Files.list(directoryPath).forEach(path -> {
                    fileNames.append(path.getFileName().toString()).append("\n");
                });

                String newFileList = fileNames.toString();
                Label currentLabel = (uploadedPDF.getChildren().isEmpty() ? null : (Label) uploadedPDF.getChildren().get(0));
                String currentFileList = (currentLabel != null ? currentLabel.getText() : "");

                if (!newFileList.equals(currentFileList)) {
                    uploadedPDF.getChildren().clear();
                    uploadedPDF.getChildren().add(new Label(newFileList));

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("List Updated");
                    alert.setHeaderText(null);
                    alert.setContentText("Updated list of uploaded certificates.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("List Check");
                    alert.setHeaderText(null);
                    alert.setContentText("List is up to date.");
                    alert.showAndWait();
                }
            } else {
                System.out.println("Directory does not exist.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The directory does not exist.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            System.out.println("Failed to list certificates: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to list certificates: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    


}




