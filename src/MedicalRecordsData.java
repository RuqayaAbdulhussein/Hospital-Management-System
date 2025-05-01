
public class MedicalRecordsData {
    private int Rid;
    private int Pid;
    private int Nid;
    private int Did;
    private String doctorName;
    private String nurseName;
    private String diagnosis;
    private String treatment;
    private String createdBy;
    private String updatedBy;

    public MedicalRecordsData(int Rid,int Pid, int Nid, String nurseName, int Did, String doctorName, String diagnosis, String treatment, String createdBy, String updatedBy){
        this.Rid = Rid;
        this.Pid = Pid;
        this.Nid = Nid;
        this.Did = Did;
        this.doctorName = doctorName;
        this.nurseName = nurseName;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public int getRid() {
        return Rid;
    }

    public int getPid() {
        return Pid;
    }
    public int getNid() {
        return Nid;
    }
    public int getDid() {
        return Did;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getNurseName() {
        return nurseName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }


    public String getTreatment() {
        return treatment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy(){
        return updatedBy;
    }
}
