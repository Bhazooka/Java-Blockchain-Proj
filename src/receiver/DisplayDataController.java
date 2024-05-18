package receiver;

import acsse.csc03a3.Block;
import acsse.csc03a3.Blockchain;
import acsse.csc03a3.Transaction;
//import blockchain.MyBlockChain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class DisplayDataController {
    @FXML private Label lblIDText;
    @FXML private Label lblNameText;
    @FXML private Label lblSurnameText;
    @FXML private ListView<String> txtDocumentsList;
    private Blockchain<String> blockchain = new Blockchain<>();
    private static List<Transaction<String>> ListTransactions = new ArrayList<>();

    public void setStudentDetails(String id, String fname, String lname) {
        lblIDText.setText(id);
        lblNameText.setText(fname);
        lblSurnameText.setText(lname);
    }

    public void loadCertificates(String studentID, String firstName, String lastName) {
        txtDocumentsList.getItems().clear();

        try (BufferedReader br = new BufferedReader(new FileReader("data/certificates.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(", ");
                if (fields.length == 4) {
                    String certID = fields[0];
                    String certFirstName = fields[1];
                    String certLastName = fields[2];
                    String certName = fields[3];

                    if (certID.equals(studentID) && certFirstName.equals(firstName) && certLastName.equals(lastName)) {
                        txtDocumentsList.getItems().add(certName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void btnApprove(ActionEvent event) {
        String selectedCertificate = txtDocumentsList.getSelectionModel().getSelectedItem();
        if (selectedCertificate == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Error");
            alert.setHeaderText(null);
            alert.setContentText("No certificate selected.");
            alert.showAndWait();
            return;
        }

        String certificatesPath = "data/certificates.txt";
        String approvedPath = "data/approved.txt";

        List<String> remainingCertificates = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(certificatesPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(", ");
                if (fields.length == 4) {
                    String certID = fields[0];
                    String certFirstName = fields[1];
                    String certLastName = fields[2];
                    String certName = fields[3];

                    if (certID.equals(lblIDText.getText()) && 
                            certFirstName.equals(lblNameText.getText()) && 
                            certLastName.equals(lblSurnameText.getText()) && 
                            certName.equals(selectedCertificate)) {
                        try (FileWriter fw = new FileWriter(approvedPath, true); BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write(line + "\n");
                        }
                        found = true;
                    } else {
                        remainingCertificates.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (found) {
            try (FileWriter fw = new FileWriter(certificatesPath); BufferedWriter bw = new BufferedWriter(fw)) {
                for (String cert : remainingCertificates) {
                    bw.write(cert + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        txtDocumentsList.getItems().remove(selectedCertificate);
    }

    
    @FXML
    void btnDecline(ActionEvent event) {
        String selectedCertificate = txtDocumentsList.getSelectionModel().getSelectedItem();
        if (selectedCertificate == null) {
            System.out.println("No certificate selected.");
            return;
        }

        String certificatesPath = "data/certificates.txt";
        String declinedPath = "data/declined.txt";

        List<String> remainingCertificates = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(certificatesPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(", ");
                if (fields.length == 4) {
                    String certID = fields[0];
                    String certFirstName = fields[1];
                    String certLastName = fields[2];
                    String certName = fields[3];

                    if (certID.equals(lblIDText.getText()) && 
                    	certFirstName.equals(lblNameText.getText()) && 
                    	certLastName.equals(lblSurnameText.getText()) && 
                    	certName.equals(selectedCertificate)) {
                    	
                        try (FileWriter fw = new FileWriter(declinedPath, true); BufferedWriter bw = new BufferedWriter(fw)) {
                            bw.write(line + "\n");
                        }
                        found = true;
                    } else {
                        remainingCertificates.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (found) {
            try (FileWriter fw = new FileWriter(certificatesPath); BufferedWriter bw = new BufferedWriter(fw)) {
                for (String cert : remainingCertificates) {
                    bw.write(cert + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            txtDocumentsList.getItems().remove(selectedCertificate);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Certificate Declined");
            alert.setHeaderText(null);
            alert.setContentText("The certificate has been successfully declined.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Certificate could not be found.");
            alert.showAndWait();
        }
    }


    @FXML
    void btnAddToBlockchain(ActionEvent event) {
        try {
            blockchain = recordBlock();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Blockchain Update");
            alert.setHeaderText(null);
            alert.setContentText("Transactions have been successfully added to the blockchain.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while adding transactions to the blockchain.");
            alert.showAndWait();
        }
    }

    public static Blockchain<String> recordBlock() throws IOException {
        Blockchain<String> BlockChain = new Blockchain<>();
        List<Transaction<String>> ListTransactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("data/approved.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 4) {
                    continue; 
                }

                String id = fields[0].trim();
                String name = fields[1].trim();
                String surname = fields[2].trim();
                String certificatePdfName = fields[3].trim();

                String sender = name + " " + surname;
                String receiver = id;
                String data = certificatePdfName;

                Transaction<String> transaction = new Transaction<>(sender, receiver, data);
                ListTransactions.add(transaction);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/transactions.txt", true))) {
            for (Transaction<String> transaction : ListTransactions) {
                String transactionOutput = transaction.toString();
                writer.write(transactionOutput + "\n");
                System.out.println(transactionOutput);
            }
        }

        handleTransactions(BlockChain, ListTransactions);

        return BlockChain;
    }

    private static void handleTransactions(Blockchain<String> myBlockchain, List<Transaction<String>> initialTransactions) throws IOException {
        int stakeAmount = 100;
        int txIndex = 0;
        int lastProcessed = 0;

        ArrayList<Transaction<String>> transactionsList = new ArrayList<>(initialTransactions);

        for (Transaction<String> transaction : initialTransactions) {
            if (txIndex == 0) {
                Block<String> genesisBlock = new Block<>("0", transactionsList.subList(0, 1));
                genesisBlock.calculateHash();

                myBlockchain.registerStake(genesisBlock.getHash(), stakeAmount);
                myBlockchain.addBlock(transactionsList.subList(0, 1));
            } else {
                ArrayList<Transaction<String>> currentTransactions = new ArrayList<>(transactionsList.subList(txIndex, txIndex + 1));

                Block<String> newBlock = new Block<>("0", currentTransactions);
                newBlock.calculateHash();

                myBlockchain.registerStake(newBlock.getHash(), stakeAmount);
                myBlockchain.addBlock(currentTransactions);

                lastProcessed = txIndex;
            }

            txIndex++;
        }

        System.out.println("Printing blockchain: \n" + myBlockchain);
        System.out.println("Checking validity: \n" + myBlockchain.isChainValid());
    }

}



