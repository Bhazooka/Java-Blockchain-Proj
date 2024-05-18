package receiver;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SenderData {

    private final StringProperty ID;
    private final StringProperty firstName;
    private final StringProperty secondName;
    private final StringProperty email;
    private final StringProperty DOB;

    public SenderData(String id, String firstname, String secondName, String email, String dob) {

        this.ID = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(firstname);
        this.secondName = new SimpleStringProperty(secondName);
        this.email = new SimpleStringProperty(email);
        this.DOB = new SimpleStringProperty(dob);

    }


    public String getID() {
        return ID.get();
    }

    public StringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getsecondName() {
        return secondName.get();
    }

    public StringProperty secondNameProperty() {
        return secondName;
    }

    public void setsecondName(String secondName) {
        this.secondName.set(secondName);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getDOB() {
        return DOB.get();
    }

    public StringProperty DOBProperty() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB.set(DOB);
    }

}
