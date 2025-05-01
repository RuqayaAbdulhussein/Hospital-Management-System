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

public class patientRegistration {
    private Scene patientRegistration;
    private Stage stage;
    private TableView<PatientData> tableView;
    ReceptionistPanel receptionistPanel = new ReceptionistPanel();
    private String userID;
    private String userFN;
    private String userLN;


    public patientRegistration(Stage primaryStage,String userID, String userFN, String userLN){
        this.stage = primaryStage;
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
        Button changePasswordButton = new Button("Change Password");


        topBar.getChildren().addAll(changePasswordButton, logoutButton);

        Label titleLabel = new Label("This is the Receptionist Panel");


        tableView = new TableView<>();
        setupTableColumns();
        loadPatientData();
        tableView.setOnMouseClicked(me -> updatePatientView());

        VBox receptionistForm = receptionistPanel.getReceptionistPanel();



        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(10, 0, 0, 0));
        buttonPane.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button appointmentsButton = new Button("Appointments");

        appointmentsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                appointments();
            }
        });

        addButton.setOnAction(event -> {
            try {
                addPatient();
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

        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changePassword();
            }
        });

        updateButton.setOnAction(event -> {
            updatePatient(new ActionEvent());
        });


        buttonPane.getChildren().addAll(receptionistPanel.clear, addButton, updateButton, appointmentsButton);

        layout.getChildren().addAll(topBar, titleLabel, tableView, receptionistForm, buttonPane);

        patientRegistration = new Scene(layout, 800, 400);
        stage.setTitle("Receptionist Scene");
        stage.setScene(patientRegistration);
        stage.setMaximized(true);
        stage.show();
    }

    private void setupTableColumns() {
        TableColumn<PatientData, String> patientIdColumn = new TableColumn<>("Patient ID");
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));


        TableColumn<PatientData, Long> qidColumn = new TableColumn<>("QID");
        qidColumn.setCellValueFactory(cellData -> cellData.getValue().qidProperty().asObject());

        TableColumn<PatientData, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<PatientData, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<PatientData, Date> dateOfBirthColumn = new TableColumn<>("Date of Birth");
        dateOfBirthColumn.setCellValueFactory(cellData -> cellData.getValue().dateOfBirthProperty());

        TableColumn<PatientData, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<PatientData, String> bloodTypeColumn = new TableColumn<>("Blood Type");
        bloodTypeColumn.setCellValueFactory(new PropertyValueFactory<>("bloodType"));

        TableColumn<PatientData, String> nationalityColumn = new TableColumn<>("Nationality");
        nationalityColumn.setCellValueFactory(new PropertyValueFactory<>("nationality"));

        TableColumn<PatientData, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<PatientData, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<PatientData, String> emergencyContactColumn = new TableColumn<>("Emergency Contact");
        emergencyContactColumn.setCellValueFactory(new PropertyValueFactory<>("emergencyContact"));

        TableColumn<PatientData, String> emergencyContactRelationshipColumn = new TableColumn<>("Emergency Contact Relationship");
        emergencyContactRelationshipColumn.setCellValueFactory(new PropertyValueFactory<>("emergency_contact_relationship"));

        TableColumn<PatientData, String> createdByColumn = new TableColumn<>("Created By: ");
        createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

        TableColumn<PatientData, String> updatedByColumn = new TableColumn<>("Last Updated By: ");
        updatedByColumn.setCellValueFactory(new PropertyValueFactory<>("updatedBy"));

        tableView.getColumns().addAll(
                patientIdColumn,qidColumn, firstNameColumn, lastNameColumn, dateOfBirthColumn,
                genderColumn, bloodTypeColumn,nationalityColumn, phoneColumn, emailColumn,
                emergencyContactColumn, emergencyContactRelationshipColumn, createdByColumn, updatedByColumn
        );
    }


    private void loadPatientData() {
        ObservableList<PatientData> patientList = FXCollections.observableArrayList();
        Connection con = DBUtils.establishConnection();
        String query = "SELECT patient_id, QID, firstname, lastname, date_of_birth, gender, blood_type,nationality, phone, email, emergency_contact, emergency_contact_relationship, created_by, last_updated_by FROM Patients";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String PatientID = rs.getString("patient_id");
                Long QID = rs.getLong("qid");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                Date dateOfBirth = rs.getDate("date_of_birth");
                String gender = rs.getString("gender");
                String bloodType = rs.getString("blood_type");
                String nationality = rs.getString("nationality");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String emergencyContact = rs.getString("emergency_contact");
                String emergecnyContactRS = rs.getString("emergency_contact_relationship");
                String createdBy = rs.getString("created_by");
                String updatedBy = rs.getString("last_updated_by");

                patientList.add(new PatientData(PatientID, QID, firstName, lastName, dateOfBirth, gender, bloodType, nationality, phone, email, emergencyContact, emergecnyContactRS, createdBy, updatedBy));
            }

            tableView.setItems(patientList);
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to connect to the database.");
        }
    }

    private void updatePatientView() {
        PatientData selectedPatient = tableView.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            receptionistPanel.QID.setText(String.valueOf(selectedPatient.getQID()));
            receptionistPanel.QID.setDisable(true);
            receptionistPanel.firstname.setText(selectedPatient.getFirstName());
            receptionistPanel.lastname.setText(selectedPatient.getLastName());
            String dateString = String.valueOf(selectedPatient.getDate());
            if (dateString != null && !dateString.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateString, formatter);
                receptionistPanel.dateOFBirth.setValue(date);
            }
            receptionistPanel.gender.setValue(selectedPatient.getGender());
            receptionistPanel.bloodType.setValue(selectedPatient.getBloodType());
            receptionistPanel.nationality.setValue(selectedPatient.getNationality());
            receptionistPanel.phone.setText(selectedPatient.getPhone());
            receptionistPanel.email.setText(selectedPatient.getEmail());
            receptionistPanel.emergencyContact.setText(selectedPatient.getEmergencyContact());
            receptionistPanel.emergencyContactRS.setValue(selectedPatient.getEmergency_contact_relationship());

        }
    }

    private void addPatient() throws NoSuchAlgorithmException {
        if (!Validation()) {
            return;
        }
        String receptionistName = userFN + " " + userLN;
        String QID = receptionistPanel.QID.getText().trim();
        String firstName = receptionistPanel.firstname.getText().trim();
        String lastName = receptionistPanel.lastname.getText().trim();
        String phone = receptionistPanel.phone.getText().trim();
        String email = receptionistPanel.email.getText().trim();
        String emergencyContact = receptionistPanel.emergencyContact.getText().trim();

        if (!checkDuplicateID(QID)){
            return;
        }

        String sql = "INSERT INTO Patients (receptionist_id, QID, firstname, lastname, date_of_birth, gender, blood_type, nationality, phone, email, emergency_contact, " +
                "emergency_contact_relationship, created_by, last_updated_by ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, userID);
            statement.setLong(2, Long.parseLong(QID));
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            LocalDate Dob = receptionistPanel.dateOFBirth.getValue();
            statement.setDate(5, java.sql.Date.valueOf(Dob));
            statement.setString(6, receptionistPanel.getSelectedGender());
            statement.setString(7, receptionistPanel.getSelectedBloodType());
            statement.setString(8, receptionistPanel.getSelectedNationality());
            statement.setString(9, phone);
            statement.setString(10, email);
            statement.setString(11, emergencyContact);
            statement.setString(12, receptionistPanel.getSelectedRS());
            statement.setString(13, receptionistName);
            statement.setString(14, null);


            statement.executeUpdate();
            clearFields();
            loadPatientData();
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void updatePatient(ActionEvent event) {
        if (!Validation()) {
            return;
        }

        PatientData selectedPatient = tableView.getSelectionModel().getSelectedItem();
        String receptionistName = userFN + " " + userLN;
        if (selectedPatient != null) {
            Alert confirmUpdateAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmUpdateAlert.setTitle("Confirm Update");
            confirmUpdateAlert.setHeaderText("Are you sure you want to update this patient's details?");

            confirmUpdateAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String sql = "UPDATE Patients SET receptionist_id = ?, QID = ?, firstname = ?, lastname = ?, date_of_birth = ?, gender = ?, blood_type = ?, nationality = ?, phone = ?, email = ?, emergency_contact = ?, emergency_contact_relationship = ?, last_updated_by = ? WHERE patient_id = ?";

                    try (Connection con = DBUtils.establishConnection();
                         PreparedStatement statement = con.prepareStatement(sql)) {
                        statement.setString(1, userID);
                        statement.setString(2, receptionistPanel.QID.getText());
                        statement.setString(3, receptionistPanel.firstname.getText());
                        statement.setString(4, receptionistPanel.lastname.getText());
                        statement.setDate(5, java.sql.Date.valueOf(receptionistPanel.dateOFBirth.getValue()));
                        statement.setString(6, receptionistPanel.getSelectedGender());
                        statement.setString(7, receptionistPanel.getSelectedBloodType());
                        statement.setString(8, receptionistPanel.getSelectedNationality());
                        statement.setString(9, receptionistPanel.phone.getText());
                        statement.setString(10, receptionistPanel.email.getText());
                        statement.setString(11, receptionistPanel.emergencyContact.getText());
                        statement.setString(12, receptionistPanel.getSelectedRS());
                        statement.setString(13, receptionistName);
                        statement.setString(14, selectedPatient.getPatientId());

                        statement.executeUpdate();
                        clearFields();
                        loadPatientData();

                        showAlertConfirm("Success", "Patient details updated successfully.");

                        DBUtils.closeConnection(con, statement);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlertError("Error", "Failed to update patient details.");
                    }
                }
            });
        } else {
            showAlertError("Error", "No patient selected to update.");
        }
    }

    private boolean Validation() {
        String QID = receptionistPanel.QID.getText().trim();
        String firstName = receptionistPanel.firstname.getText().trim();
        String lastName = receptionistPanel.lastname.getText().trim();
        LocalDate dob = receptionistPanel.dateOFBirth.getValue();
        String DOB = (dob != null) ? dob.toString() : "";
        String phone = receptionistPanel.phone.getText().trim();
        String email = receptionistPanel.email.getText().trim();
        String emergencyContact = receptionistPanel.emergencyContact.getText().trim();

        if (DOB == null || DOB.isEmpty()) {
            showAlertError("Validation Error", "Date cannot be empty.");
            return false;
        }
        if (receptionistPanel.getSelectedGender() == null || receptionistPanel.getSelectedGender().isEmpty()) {
            showAlertError("Validation Error", "Gender cannot be empty.");
            return false;
        }
        if (receptionistPanel.getSelectedBloodType() == null || receptionistPanel.getSelectedBloodType().isEmpty()) {
            showAlertError("Validation Error", "Blood Type cannot be empty.");
            return false;
        }
        if (receptionistPanel.getSelectedNationality() == null || receptionistPanel.getSelectedNationality().isEmpty()) {
            showAlertError("Validation Error", "Nationality cannot be empty.");
            return false;
        }
        if (receptionistPanel.getSelectedRS() == null || receptionistPanel.getSelectedRS().isEmpty()) {
            showAlertError("Validation Error", "Emergency Contact Relationship cannot be empty.");
            return false;
        }

        String QIDRegex = "^\\d{11}$";
        String nameRegex = "^[a-zA-Z]{3,50}$";
        String emailRegex = "^[\\w-\\.]+@[\\w-]+\\.[\\w-]{2,63}$";
        String phoneRegex = "^\\d{8}$";

        Pattern qidPattern = Pattern.compile(QIDRegex);
        Pattern namePattern = Pattern.compile(nameRegex);
        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern phonePattern = Pattern.compile(phoneRegex);

        Matcher matchQID = qidPattern.matcher(QID);
        Matcher matchFname = namePattern.matcher(firstName);
        Matcher matchLname = namePattern.matcher(lastName);
        Matcher matchemail = emailPattern.matcher(email);
        Matcher matchphone = phonePattern.matcher(phone);
        Matcher matchEmergencyphone = phonePattern.matcher(emergencyContact);

        if (!matchQID.matches() || QID.isEmpty()) {
            showAlertError("Validation Error", "QID must be filled out and a 11-digit number.");
            return false;
        }
        if (!matchFname.matches() || firstName.isEmpty()) {
            showAlertError("Validation Error", "First name must be filled out and contain characters only.");
            return false;
        }
        if (!matchLname.matches() || lastName.isEmpty()) {
            showAlertError("Validation Error", "Last name must be filled out and contain characters only.");
            return false;
        }
        if (!matchemail.matches() || email.isEmpty()) {
            showAlertError("Validation Error", "Invalid email format.");
            return false;
        }
        if (!matchphone.matches() || phone.isEmpty()) {
            showAlertError("Validation Error", "Phone number must be exactly 8 digits (qatari number).");
            return false;
        }

        if (!matchEmergencyphone.matches() || emergencyContact.isEmpty()) {
            showAlertError("Validation Error", "Emergency contact number must be exactly 8 digits (qatari number).");
            return false;
        }
        return true;
    }

    private void clearFields() {
        receptionistPanel.QID.setDisable(false);
        receptionistPanel.QID.clear();
        receptionistPanel.firstname.clear();
        receptionistPanel.lastname.clear();
        receptionistPanel.phone.clear();
        receptionistPanel.email.clear();
        receptionistPanel.emergencyContact.clear();
        receptionistPanel.dateOFBirth.setValue(null);
        receptionistPanel.gender.setValue(null);
        receptionistPanel.bloodType.setValue(null);
        receptionistPanel.nationality.setValue(null);
        receptionistPanel.emergencyContactRS.setValue(null);
    }

    private boolean checkDuplicateID(String QID) {
        String sql = "SELECT COUNT(*) FROM Patients WHERE QID = ?";
        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, QID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    showAlertError("Duplicate QID", "A patient with this QID already exists.");
                    return false;
                }
            }
            DBUtils.closeConnection(con, statement);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to check for duplicate QID.");
        }
        return true;
    }

    private void appointments(){ //redirects to the appointments page specific to the selected patient
        PatientData selectedPatient = tableView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlertError("Selection Error", "Please select a patient before proceeding to appointments.");
            return;
        }
        String patientId = selectedPatient.getPatientId();
        String patientFN = selectedPatient.getFirstName();
        String patientLN = selectedPatient.getLastName();
        Appointments appointments= new Appointments(stage, patientId, patientFN, patientLN, userID, userFN, userLN);
        appointments.initializeComponents();
    }


    private void logout(){
        userLogin login = new userLogin(stage);
        login.initializeComponents();
    }

    private void changePassword(){
        changePassword changePass = new changePassword(stage, userID, userFN, userLN);
        changePass.initializeComponents();
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
