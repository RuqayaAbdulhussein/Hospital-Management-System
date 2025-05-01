import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
//this class fetches and displays appointments a patient may have, this is accessible by a receptionist.
public class Appointments {
    private Scene AppointmentsScene;
    private Stage stage;
    private TableView<AppointmentsData> tableView;
    AppointmentPanel appointmentPanel = new AppointmentPanel();
    private String patientID;
    private String patientFN;
    private String patientLN;
    private String userID;
    private String userFN;
    private String userLN;

    public Appointments(Stage primaryStage, String patientID, String patientFN, String patientLN, String userID, String userFN, String userLN){
        this.stage = primaryStage;
        this.patientID = patientID;
        this.patientFN = patientFN;
        this.patientLN = patientLN;
        this.userID =userID;
        this.userFN =userFN;
        this.userLN =userLN;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(5, 10, 5, 10));

        Button logoutButton = new Button("Log out");
        Button BackButton = new Button("Back");


        topBar.getChildren().addAll(BackButton, logoutButton);

        Label titleLabel = new Label("Appointments for " + patientFN + " " +patientLN);


        tableView = new TableView<>();
        setupTableColumns();
        loadAppointmentData(patientID);
        tableView.setOnMouseClicked(me -> updateUserView());

        VBox appointmentForm = appointmentPanel.getappointmentPanel();

        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(10, 0, 0, 0));
        buttonPane.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");

        addButton.setOnAction(event -> {
            try {
                addAppointment();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                showAlertError("Error", "Password encryption failed. Please try again.");
            }
        });

        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logout();
            }
        });

        BackButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                back();
            }
        });

        updateButton.setOnAction(event -> {
            updateAppointment(new ActionEvent());
        });


        buttonPane.getChildren().addAll(appointmentPanel.clear, addButton, updateButton);

        layout.getChildren().addAll(topBar, titleLabel, tableView, appointmentForm, buttonPane);

        AppointmentsScene = new Scene(layout, 800, 400);
        stage.setTitle("Appointment Scene");
        stage.setScene(AppointmentsScene);
        stage.setMaximized(true);
        stage.show();
    }

    private void setupTableColumns() {
        TableColumn<AppointmentsData, String> appointmentIDColumn = new TableColumn<>("Appointment ID: ");
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("Aid"));

        TableColumn<AppointmentsData, String> receptionistIDColumn = new TableColumn<>("Receptionist ID: ");
        receptionistIDColumn.setCellValueFactory(new PropertyValueFactory<>("Rid"));

        TableColumn<AppointmentsData, String> PidColumn = new TableColumn<>("Patient ID");
        PidColumn.setCellValueFactory(new PropertyValueFactory<>("Pid"));

        TableColumn<AppointmentsData, String> DoctorNameColumn = new TableColumn<>("Doctor Name");
        DoctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<AppointmentsData, Date> dateColumn = new TableColumn<>("Date of Appointment: ");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().DateOfAppointmentProperty());

        TableColumn<AppointmentsData, String> TimeColumn = new TableColumn<>("Appointment Time: ");
        TimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));

        TableColumn<AppointmentsData, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<AppointmentsData, String> POVColumn = new TableColumn<>("Purpose of Visit");
        POVColumn.setCellValueFactory(new PropertyValueFactory<>("POV"));

        TableColumn<AppointmentsData, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<AppointmentsData, String> createdByColumn = new TableColumn<>("Created By: ");
        createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

        TableColumn<AppointmentsData, String> updatedByColumn = new TableColumn<>("Last Updated By: ");
        updatedByColumn.setCellValueFactory(new PropertyValueFactory<>("updatedBy"));


        tableView.getColumns().addAll(appointmentIDColumn, receptionistIDColumn, PidColumn, DoctorNameColumn, dateColumn, TimeColumn, departmentColumn, POVColumn, statusColumn, createdByColumn, updatedByColumn);
    }

    private void loadAppointmentData(String patientID) {
        ObservableList<AppointmentsData> appointmentList = FXCollections.observableArrayList();
        Connection con = DBUtils.establishConnection();
        String query = "SELECT appointment_id, receptionist_id, patient_id, doctor_name, date, time, department, purpose_of_visit, status, created_by, last_updated_by FROM Appointments WHERE patient_id = ?";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, patientID);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                int receptionistId = rs.getInt("receptionist_id");
                int patientId = rs.getInt("patient_id");
                String doctorName = rs.getString("doctor_name");
                Date dateOfAppointment = rs.getDate("date");
                String time = rs.getString("time");
                String department = rs.getString("department");
                String pov = rs.getString("purpose_of_visit");
                String status = rs.getString("status");
                String createdBy = rs.getString("created_by");
                String updatedBy = rs.getString("last_updated_by");

                appointmentList.add(new AppointmentsData(appointmentId, receptionistId,patientId, doctorName, dateOfAppointment, time, department, pov, status, createdBy, updatedBy));
            }

            tableView.setItems(appointmentList);
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to load appointment data.");
        }
    }

    private void updateUserView() {
        AppointmentsData selectedAppointment = tableView.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            appointmentPanel.DoctorName.setValue(String.valueOf(selectedAppointment.getDoctorName()));
            String dateString = String.valueOf(selectedAppointment.getDate());
            if (dateString != null && !dateString.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateString, formatter);
                appointmentPanel.dateofAppointment.setValue(date);
            }
            appointmentPanel.time.setValue(String.valueOf(selectedAppointment.getAppointmentTime()));
            appointmentPanel.department.setValue(selectedAppointment.getDepartment());
            appointmentPanel.POV.setText(selectedAppointment.getPOV());
            appointmentPanel.status.setValue(selectedAppointment.getStatus());
        }
    }

    private void addAppointment() throws NoSuchAlgorithmException {
        if (!Validation()) {
            return;
        }

        String purposeOfVisit = appointmentPanel.POV.getText().trim();
        String receptionistName = userFN + " " + userLN;
        String doctorName = appointmentPanel.getSelectedDoctor();
        LocalDate appointmentDate = appointmentPanel.dateofAppointment.getValue();
        String appointmentTime = appointmentPanel.getSelectedTime();

        if (checkDuplicateID(patientID, doctorName, appointmentDate, appointmentTime)) {// prevents receptionist from creating several appointments that are the same
            //this is also a mitigation for appointment overbooking (DoS)
            showAlertError("Duplicate Appointment", "An appointment with this doctor at the same time already exists.");
            return;
        }
        String sql = "INSERT INTO Appointments (receptionist_id, patient_id, doctor_name, date, time, department, purpose_of_visit, status, created_by, last_updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, userID);
            statement.setString(2, patientID);
            statement.setString(3, appointmentPanel.getSelectedDoctor());
            LocalDate DateofApp = appointmentPanel.dateofAppointment.getValue();
            statement.setDate(4, java.sql.Date.valueOf(DateofApp));
            statement.setString(5, appointmentPanel.getSelectedTime());
            statement.setString(6, appointmentPanel.getSelectedDepartment());
            statement.setString(7, purposeOfVisit);
            statement.setString(8, appointmentPanel.getSelectedStatus());
            statement.setString(9, receptionistName);
            statement.setString(10, null);

            statement.executeUpdate();
            clearFields();
            loadAppointmentData(patientID);
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateAppointment(ActionEvent event) {
        if (!Validation()) {
            return;
        }
        String receptionistName = userFN + " " + userLN;
        AppointmentsData selectedAppointment = tableView.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            Alert confirmUpdateAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmUpdateAlert.setTitle("Confirm Update");
            confirmUpdateAlert.setHeaderText("Are you sure you want to update this appointment's details?");
            confirmUpdateAlert.setContentText("This action cannot be undone.");

            confirmUpdateAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String sql = "UPDATE Appointments SET receptionist_id = ?, patient_id = ?, doctor_name = ?, date = ?, time = ?, department = ?, purpose_of_visit = ?, status = ?, last_updated_by = ? WHERE appointment_id = ?";

                    try (Connection con = DBUtils.establishConnection();
                         PreparedStatement statement = con.prepareStatement(sql)) {
                        statement.setString(1, userID);
                        statement.setString(2, patientID);
                        statement.setString(3, appointmentPanel.getSelectedDoctor());
                        statement.setDate(4, java.sql.Date.valueOf(appointmentPanel.dateofAppointment.getValue()));
                        statement.setString(5, appointmentPanel.getSelectedTime());
                        statement.setString(6, appointmentPanel.getSelectedDepartment());
                        statement.setString(7, appointmentPanel.POV.getText());
                        statement.setString(8, appointmentPanel.getSelectedStatus());
                        statement.setString(9,receptionistName);
                        statement.setInt(10, selectedAppointment.getAid());

                        statement.executeUpdate();
                        clearFields();
                        loadAppointmentData(patientID);

                        showAlertConfirm("Success", "Appointment details updated successfully.");

                        DBUtils.closeConnection(con, statement);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlertError("Error", "Failed to update appointment details.");
                    }
                }
            });
        } else {
            showAlertError("Error", "No appointment selected to update.");
        }
    }

    private boolean Validation() {
        LocalDate dofA = appointmentPanel.dateofAppointment.getValue();
        String DateOfAppointment = (dofA != null) ? dofA.toString() : "";
        String POV = appointmentPanel.POV.getText().trim();

        if (appointmentPanel.getSelectedDoctor() == null || appointmentPanel.getSelectedDoctor().isEmpty()) {
            showAlertError("Validation Error", "Doctor name cannot be empty.");
            return false;
        }
        if (DateOfAppointment == null || DateOfAppointment.isEmpty()) {
            showAlertError("Validation Error", "Date cannot be empty.");
            return false;
        }
        if (appointmentPanel.getSelectedTime() == null || appointmentPanel.getSelectedTime().isEmpty()) {
            showAlertError("Validation Error", "Time cannot be empty.");
            return false;
        }
        if (appointmentPanel.getSelectedDepartment() == null || appointmentPanel.getSelectedDepartment().isEmpty()) {
            showAlertError("Validation Error", "Department cannot be empty.");
            return false;
        }
        if (appointmentPanel.getSelectedStatus() == null || appointmentPanel.getSelectedStatus().isEmpty()) {
            showAlertError("Validation Error", "Status cannot be empty.");
            return false;
        }

        String POVRegex = "^(?=.*[a-zA-Z])[a-zA-Z0-9.,'() -]{5,100}$";


        Pattern POVPattern = Pattern.compile(POVRegex);

        Matcher matchPOV = POVPattern.matcher(POV);

        if (!matchPOV.matches() || POV.isEmpty()) {
            showAlertError("Validation Error", "Purpose of visit must be filed (allowed characters . , ' () -)");
            return false;
        }
        return true;
    }

    private void clearFields() {

        appointmentPanel.DoctorName.setValue(null);
        appointmentPanel.dateofAppointment.setValue(null);
        appointmentPanel.time.setValue(null);
        appointmentPanel.department.setValue(null);
        appointmentPanel.POV.clear();
        appointmentPanel.status.setValue(null);
    }

    private boolean checkDuplicateID(String patientID, String doctorName, LocalDate date, String time) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE patient_id = ? AND doctor_name = ? AND date = ? AND time = ?";

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, patientID);
            statement.setString(2, doctorName);
            statement.setDate(3, java.sql.Date.valueOf(date));
            statement.setString(4, time);

            ResultSet rs = statement.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
            DBUtils.closeConnection(con, statement);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to check for duplicate appointments.");
        }

        return false;
    }

    private void back(){
        patientRegistration pateintReg = new patientRegistration(stage, userID, userFN, userLN);
        pateintReg.initializeComponents();
    }

    private void logout(){
        userLogin login = new userLogin(stage);
        login.initializeComponents();
    }


    private void showAlertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlertConfirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
