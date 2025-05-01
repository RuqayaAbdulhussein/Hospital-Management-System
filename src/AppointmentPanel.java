import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//this is the pane that specifies the fields displayed at the bottom of the appointments page.
public class AppointmentPanel {

    ComboBox<String> DoctorName = new ComboBox<>();
    DatePicker dateofAppointment = new DatePicker();
    ComboBox<String> time = new ComboBox<>();
    ComboBox<String> department = new ComboBox<>();
    TextArea POV = new TextArea();
    ComboBox<String> status = new ComboBox<>();
    Button clear = new Button("Clear");

    public VBox getappointmentPanel() {
        VBox appointmentPane = new VBox(10);
        appointmentPane.setAlignment(Pos.CENTER);

        HBox fieldsPane = new HBox(10);
        fieldsPane.setAlignment(Pos.CENTER);

        Label DNameLabel = createRequiredLabel("Doctor Name: ");
        Label DateLabel = createRequiredLabel("Date of Appointment: ");
        Label TimeLabel = createRequiredLabel("Time of Appointment: ");
        Label depLabel = createRequiredLabel("Department: ");
        Label POVLabel = createRequiredLabel("Purpose of Visit: ");
        Label statusLabel = createRequiredLabel("Status: ");

        DoctorName.setPrefWidth(160);
        dateofAppointment.setPrefWidth(110);
        time.setPrefWidth(90);
        department.setPrefWidth(130);
        POV.setPrefWidth(250);
        status.setPrefWidth(110);

        populateDoctorComboBox();
        time.getItems().addAll("06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00",
                "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
                "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00");
        department.getItems().addAll("Emergency","Outpatient","Inpatient","Surgery","ICU","Radiology","Laboratory","Maternity","Physiotherapy","Cardiology","Neurology","Orthopedics","Pediatrics");
        status.getItems().addAll("Scheduled","Completed","Cancelled","No-Show");


        clear.setOnAction(e -> clearView());

        fieldsPane.getChildren().addAll(
                DNameLabel, DoctorName,
                DateLabel, dateofAppointment,
                TimeLabel, time,
                depLabel, department,
                POVLabel, POV,
                statusLabel, status
        );
        HBox povPane = new HBox(10);
        povPane.setAlignment(Pos.CENTER);
        povPane.getChildren().addAll(POVLabel, POV);

        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.getChildren().add(clear);

        appointmentPane.getChildren().addAll(fieldsPane, povPane ,buttonPane);

        return appointmentPane;
    }

    private Label createRequiredLabel(String labelText) {
        Label label = new Label(labelText);
        Text asterisk = new Text(" *");
        asterisk.setFill(Color.RED);
        label.setGraphic(asterisk);
        return label;
    }

    public String getSelectedDoctor() {
        return DoctorName.getValue();
    }

    public String getSelectedTime() {
        return time.getValue();
    }

    public String getSelectedDepartment() {
        return department.getValue();
    }
//dynamically fetches doctors from the database and adds them to the combo box, instead of hardcoding their names like usual with the other combo boxes, list of doctors is not fixed.
    private void populateDoctorComboBox() {
        DoctorName.getItems().clear();
        String sql = "SELECT firstname, lastname FROM Users WHERE role = 'doctor'";
        try (Connection con = DBUtils.establishConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String doctorName = firstName + " " + lastName;
                DoctorName.getItems().add(doctorName);
            }
            DBUtils.closeConnection(con, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Error", "Failed to fetch doctor names from database.");
        }
    }


    public String getSelectedStatus() {
        return status.getValue();
    }

    private void clearView() {
        DoctorName.setValue(null);
        dateofAppointment.setValue(null);
        time.setValue(null);
        department.setValue(null);
        POV.clear();
        status.setValue(null);


    }
    private void showAlertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
