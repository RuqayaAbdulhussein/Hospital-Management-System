import java.util.Date;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PatientData {

    private String patientId;
    private final LongProperty qid = new SimpleLongProperty();
    private String firstName;
    private String lastName;
    private final SimpleObjectProperty<Date> date_of_birth;
    private String gender;
    private String bloodType;
    private String nationality;
    private String phone;
    private String email;
    private String emergency_contact;
    private String emergency_contact_relationship;
    private String createdBy;
    private String updatedBy;

    public PatientData(String patientId, Long qid, String firstName, String lastName, Date date_of_birth, String gender, String bloodType,String nationality, String phone, String email, String emergency_contact, String emergency_contact_relationship, String createdBy, String updatedBy) {
        this.qid.set(qid);
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date_of_birth  = new SimpleObjectProperty<>(date_of_birth );
        this.gender = gender;
        this.bloodType = bloodType;
        this.nationality = nationality;
        this.phone = phone;
        this.email = email;
        this.emergency_contact = emergency_contact;
        this.emergency_contact_relationship = emergency_contact_relationship;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public String getPatientId() {
        return patientId;
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

    public Date getDate() {
        return date_of_birth.get();
    }

    public ObjectProperty<Date> dateOfBirthProperty() {
        return date_of_birth;
    }


    public String getGender() {
        return gender;
    }

    public String getBloodType(){
        return bloodType;
    }

    public String getNationality() {
        return nationality;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getEmergencyContact() {
        return emergency_contact;
    }

    public String getEmergency_contact_relationship() {
        return emergency_contact_relationship;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy(){
        return updatedBy;
    }
}
