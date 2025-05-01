import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
//this class represents a user model for a javaFX tableview, storing user details such as ID, QID, name, email, phone, role, specialization, and department.
//It uses javaFXs `LongProperty` for QID to enable dynamic table updates and provides getter methods for data retrieval.
public class UserData {
    private int id;
    private final LongProperty qid = new SimpleLongProperty();
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private String specialization;
    private String department;
    private String createdBy;
    private String updatedBy;

    public UserData(int id,  Long qid, String firstName, String lastName, String email, String phone, String role, String specialization, String department, String createdBy, String updatedBy) {
        this.id = id;
        this.qid.set(qid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.specialization = specialization;
        this.department = department;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public int getId() {
        return id;
    }

    public Long getQID() {
        return qid.get();
    }

    public LongProperty qidProperty() {
        return qid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getDepartment() {
        return department;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy(){
        return updatedBy;
    }

}
