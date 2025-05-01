import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.Date;

public class AppointmentsData {
    private int Aid;
    private int Rid;
    private int Pid;
    private String doctorName;
    private final SimpleObjectProperty<Date> appointmentDate;
    private String appointmentTime;
    private String department;
    private String POV;
    private String status;
    private String createdBy;
    private String updatedBy;

    public AppointmentsData(int Aid, int Rid,int Pid, String doctorName, Date appointmentDate, String appointmentTime, String department, String POV, String status, String createdBy, String updatedBy) {
        this.Aid = Aid;
        this.Rid = Rid;
        this.Pid = Pid;
        this.doctorName = doctorName;
        this.appointmentDate = new SimpleObjectProperty<>(appointmentDate);
        this.appointmentTime = appointmentTime;
        this.department = department;
        this.POV = POV;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public int getAid() {
        return Aid;
    }

    public int getRid() {
        return Rid;
    }

    public int getPid() {
        return Pid;
    }


    public String getDoctorName() {
        return doctorName;
    }

    public Date getDate() {
        return appointmentDate.get();
    }

    public ObjectProperty<Date> DateOfAppointmentProperty() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getDepartment() {
        return department;
    }

    public String getPOV() {
        return POV;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy(){
        return updatedBy;
    }
}
