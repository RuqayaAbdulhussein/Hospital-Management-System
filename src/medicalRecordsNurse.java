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
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
//this is the page the nurse gets when requesting medical records, its the same as the doctors one, the main difference is that it does not allow the nurse to provide the treatment (perscribe medication)
public class medicalRecordsNurse {
    private Scene medicalRecordsDocScene;
    private Stage stage;
    private TableView<MedicalRecordsData> tableView;
    NursePanel nursePanel = new NursePanel();
    private String patientID;
    private String userID;
    private String userFN;
    private String userLN;


    public medicalRecordsNurse(Stage primaryStage, String patientID, String userID, String userFN, String userLN){
        this.stage = primaryStage;
        this.patientID = patientID;
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


        tableView = new TableView<>();
        setupTableColumns();
        loadAppointmentData(patientID);
        tableView.setOnMouseClicked(me -> updateUserView());

        VBox doctorForm = nursePanel.getNursePane();



        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(10, 0, 0, 0));
        buttonPane.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");

        addButton.setOnAction(event -> {
            try {
                addRecord();
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


        buttonPane.getChildren().addAll(nursePanel.clear, addButton, updateButton);

        layout.getChildren().addAll(topBar, tableView, doctorForm, buttonPane);

        medicalRecordsDocScene = new Scene(layout, 800, 400);
        stage.setTitle("Medical Records Scene");
        stage.setScene(medicalRecordsDocScene);
        stage.setMaximized(true);
        stage.show();
        displayPatientInfo();
    }

    private void displayPatientInfo() {
        String query = "SELECT firstname, lastname, date_of_birth, gender, blood_type, nationality FROM Patients WHERE patient_id = ?";
        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setString(1, patientID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                Date dateOfBirth = resultSet.getDate("date_of_birth");
                String gender = resultSet.getString("gender");
                String bloodType = resultSet.getString("blood_type");
                String nationality = resultSet.getString("nationality");


                int age = calculateAge(dateOfBirth);

                Label nameLabel = new Label("Name: " + firstName + " " + lastName);
                Label ageLabel = new Label("Age: " + age);
                Label genderLabel = new Label("Gender: " + gender);
                Label bloodTypeLabel = new Label("Blood Type: " + bloodType);
                Label nationalityLabel = new Label("Nationality: " + nationality);

                VBox patientInfoBox = new VBox(10);
                patientInfoBox.setAlignment(Pos.CENTER);
                patientInfoBox.setPadding(new Insets(20));
                patientInfoBox.getChildren().addAll(nameLabel, ageLabel, genderLabel, bloodTypeLabel, nationalityLabel);

                VBox layout = (VBox) medicalRecordsDocScene.getRoot();
                layout.getChildren().add(0, patientInfoBox);
            }

            DBUtils.closeConnection(con, statement);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Error", "Failed to load patient information.");
        }
    }

    private int calculateAge(Date dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }

        LocalDate birthDate = ((java.sql.Date) dateOfBirth).toLocalDate();
        LocalDate currentDate = LocalDate.now();

        return Period.between(birthDate, currentDate).getYears();
    }




    private void setupTableColumns() {
        TableColumn<MedicalRecordsData, String> recordIDColumn = new TableColumn<>("Record ID: ");
        recordIDColumn.setCellValueFactory(new PropertyValueFactory<>("Rid"));

        TableColumn<MedicalRecordsData, String> PidColumn = new TableColumn<>("Patient ID");
        PidColumn.setCellValueFactory(new PropertyValueFactory<>("Pid"));

        TableColumn<MedicalRecordsData, String> NidColumn = new TableColumn<>("Nurse ID");
        NidColumn.setCellValueFactory(new PropertyValueFactory<>("Nid"));

        TableColumn<MedicalRecordsData, String> NurseNameColumn = new TableColumn<>("Nurse Name");
        NurseNameColumn.setCellValueFactory(new PropertyValueFactory<>("nurseName"));

        TableColumn<MedicalRecordsData, String> DidColumn = new TableColumn<>("Doctor ID");
        DidColumn.setCellValueFactory(new PropertyValueFactory<>("Did"));

        TableColumn<MedicalRecordsData, String> DoctorNameColumn = new TableColumn<>("Doctor Name");
        DoctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<MedicalRecordsData, String> diagnosisColumn = new TableColumn<>("Diagnosis: ");
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        TableColumn<MedicalRecordsData, String> treatmentColumn = new TableColumn<>("Treatment: ");
        treatmentColumn.setCellValueFactory(new PropertyValueFactory<>("treatment"));

        TableColumn<MedicalRecordsData, String> createdByColumn = new TableColumn<>("Created By: ");
        createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

        TableColumn<MedicalRecordsData, String> updatedByColumn = new TableColumn<>("Last Updated By: ");
        updatedByColumn.setCellValueFactory(new PropertyValueFactory<>("updatedBy"));

        tableView.getColumns().addAll(recordIDColumn, PidColumn, NidColumn, NurseNameColumn, DidColumn, DoctorNameColumn, diagnosisColumn, treatmentColumn, createdByColumn, updatedByColumn);
    }

    private void loadAppointmentData(String patientID) {
        ObservableList<MedicalRecordsData> medicalRecordsList = FXCollections.observableArrayList();
        Connection con = DBUtils.establishConnection();
        String query = "SELECT record_id, patient_id, nurse_id, nurse_name, doctor_id, doctor_name, diagnosis, treatment, created_by, last_updated_by FROM MedicalRecords WHERE patient_id = ?";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, patientID);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int recordId = rs.getInt("record_id");
                int patientId = rs.getInt("patient_id");
                int nurseId = rs.getInt("nurse_id");
                String nurseName = rs.getString("nurse_name");
                int doctorId = rs.getInt("doctor_id");
                String doctorName = rs.getString("doctor_name");
                String diagnosis = rs.getString("diagnosis");
                String treatment = rs.getString("treatment");
                String createdBy = rs.getString("created_by");
                String updatedBy = rs.getString("last_updated_by");

                medicalRecordsList.add(new MedicalRecordsData(recordId, patientId, nurseId, nurseName, doctorId, doctorName, diagnosis, treatment, createdBy, updatedBy));
            }

            tableView.setItems(medicalRecordsList);
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to load appointment data.");
        }
    }

    private void updateUserView() {
        MedicalRecordsData selectedRecord = tableView.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            nursePanel.DoctorName.setValue(String.valueOf(selectedRecord.getDoctorName()));
            nursePanel.diagnosis.setText(String.valueOf(selectedRecord.getDiagnosis()));
        }
    }

    private void addRecord() throws NoSuchAlgorithmException {
        String nurseName = userFN + " " + userLN;
        String diagnosis = nursePanel.diagnosis.getText().trim();
        if (checkDuplicaterecord(patientID, nurseName, diagnosis)) {
            showAlertError("Duplicate Record", "A medical record with the same details already exists.");
            return;
        }

        if (!Validation()) {
            return;
        }

        String doctorName = nursePanel.getSelectedDoctor().trim();

        int lastSpaceIndex = doctorName.lastIndexOf(" ");

        if (lastSpaceIndex == -1) {
            return;
        }

        String doctorFirstName = doctorName.substring(0, lastSpaceIndex).trim();
        String doctorLastName = doctorName.substring(lastSpaceIndex + 1).trim();
        String DoctorID = getDoctorID(doctorFirstName, doctorLastName);

//this will insert the inputted details as well as the nurses name that created this record for logging purposes
        String sql = "INSERT INTO MedicalRecords (patient_id, nurse_id, nurse_name, doctor_id, doctor_name, diagnosis, treatment, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, patientID);
            statement.setString(2, userID);
            statement.setString(3, nurseName);
            statement.setString(4, DoctorID);
            statement.setString(5, nursePanel.getSelectedDoctor());
            statement.setString(6, diagnosis);
            statement.setString(7, null);
            statement.setString(8, nurseName);

            statement.executeUpdate();
            clearFields();
            loadAppointmentData(patientID);
            DBUtils.closeConnection(con, statement);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String getDoctorID(String firstName, String lastName) {
        String doctorID = null;
        Connection con = DBUtils.establishConnection();
        String query = "SELECT id FROM Users WHERE firstname = ? AND lastname = ?";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                doctorID = rs.getString("id");
            } else {
                showAlertError("Error", "Doctor with name " + firstName + " " + lastName + " not found.");
            }

            DBUtils.closeConnection(con, statement);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Database Error", "An error occurred while retrieving the doctor's ID.");
        }

        return doctorID;
    }



    private void updateAppointment(ActionEvent event) {
        String nurseName = userFN + " " + userLN;
        if (!Validation()) {
            return;
        }
        MedicalRecordsData selectedRecord = tableView.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            if (!isAuthorizedToEdit(selectedRecord)) {
                showAlertError("Unauthorized", "You are not authorized to edit this record.");
                return;
            }}

        String doctorName = nursePanel.getSelectedDoctor().trim();

        int lastSpaceIndex = doctorName.lastIndexOf(" ");

        if (lastSpaceIndex == -1) {
            return;
        }

        String doctorFirstName = doctorName.substring(0, lastSpaceIndex).trim();
        String doctorLastName = doctorName.substring(lastSpaceIndex + 1).trim();
        String DoctorID = getDoctorID(doctorFirstName, doctorLastName);


        if (selectedRecord != null) {
            Alert confirmUpdateAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmUpdateAlert.setTitle("Confirm Update");
            confirmUpdateAlert.setHeaderText("Are you sure you want to update this record?");
            confirmUpdateAlert.setContentText("This action cannot be undone.");

            confirmUpdateAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String sql = "UPDATE MedicalRecords SET patient_id = ?, doctor_id = ?, doctor_name = ?, diagnosis = ?, last_updated_by = ? WHERE record_id = ?";

                    try (Connection con = DBUtils.establishConnection();
                         PreparedStatement statement = con.prepareStatement(sql)) {
                        statement.setString(1, patientID);
                        statement.setString(2, DoctorID);
                        statement.setString(3, nursePanel.getSelectedDoctor());
                        statement.setString(4, nursePanel.diagnosis.getText());
                        statement.setString(5, nurseName);
                        statement.setInt(6, selectedRecord.getRid());

                        statement.executeUpdate();
                        clearFields();
                        loadAppointmentData(patientID);

                        showAlertConfirm("Success", "Record details updated successfully.");

                        DBUtils.closeConnection(con, statement);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlertError("Error", "Failed to update record details.");
                    }
                }
            });
        } else {
            showAlertError("Error", "No appointment selected to update.");
        }
    }

    private boolean Validation() {
        String diagnosis = nursePanel.diagnosis.getText().trim();

        if (nursePanel.getSelectedDoctor() == null || nursePanel.getSelectedDoctor().isEmpty()) {
            showAlertError("Validation Error", "Doctor name cannot be empty.");
            return false;
        }

        String textRegex = "^(?=.*[a-zA-Z])[a-zA-Z0-9.,'()\\n -:]{5,500}$";

        Pattern textPattern = Pattern.compile(textRegex);

        Matcher matchdiagnosis = textPattern.matcher(diagnosis);

        if (!matchdiagnosis.matches() || diagnosis.isEmpty()) {
            showAlertError("Validation Error", "Diagnosis must be filled (allowed characters . , ' () -:) with a max of 500 characters");
            return false;
        }
        return true;
    }

    private void clearFields() {
        nursePanel.DoctorName.setValue(null);
        nursePanel.diagnosis.clear();
    }
    private boolean isAuthorizedToEdit(MedicalRecordsData record) {
        try {
            int userIdInt = Integer.parseInt(userID);
            return userIdInt == record.getDid() || userIdInt == record.getNid();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkDuplicaterecord(String patientID, String doctorName, String diagnosis) {
        String sql = "SELECT COUNT(*) FROM MedicalRecords WHERE patient_id = ? AND nurse_name = ? AND diagnosis = ?";
        String nurseName = userFN + " " + userLN;
        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, patientID);
            statement.setString(2, nurseName);
            statement.setString(3, diagnosis);

            ResultSet rs = statement.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }

            DBUtils.closeConnection(con, statement);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to check for duplicate medical records.");
        }

        return false;
    }


    private void back(){
        patientRecordsDocNurse pateintRecDocNurse = new patientRecordsDocNurse(stage, userID, userFN, userLN);
        pateintRecDocNurse.initializeComponents();
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
