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
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
//this displays a table with all patients, both nurse and doctor can access this to the view a specific patients medical records.
public class patientRecordsDocNurse {
    private Scene patientRegistration;
    private Stage stage;
    private TableView<PatientData> tableView;
    private String userID;
    private String userFN;
    private String userLN;

    public patientRecordsDocNurse(Stage primaryStage, String userID, String userFN, String userLN){
        this.stage = primaryStage;
        this.userID = userID;
        this.userFN = userFN;
        this.userLN = userLN;
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

        Label titleLabel = new Label("This is the Medical Staff Panel");


        tableView = new TableView<>();
        setupTableColumns();
        loadPatientData();


        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(10, 0, 0, 0));
        buttonPane.setAlignment(Pos.CENTER);

        Button medicalRecordsButton = new Button("Medical Records");

        medicalRecordsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                medicalRecords();
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

        buttonPane.getChildren().addAll(medicalRecordsButton);

        layout.getChildren().addAll(topBar, titleLabel, tableView, buttonPane);

        patientRegistration = new Scene(layout, 800, 400);
        stage.setTitle("Medical Staff Scene");
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

        tableView.getColumns().addAll(
                patientIdColumn,qidColumn, firstNameColumn, lastNameColumn, dateOfBirthColumn,
                genderColumn, nationalityColumn, phoneColumn, emailColumn,
                emergencyContactColumn, emergencyContactRelationshipColumn
        );
    }


    private void loadPatientData() {
        ObservableList<PatientData> patientList = FXCollections.observableArrayList();
        Connection con = DBUtils.establishConnection();
        String query = "SELECT patient_id, QID, firstname, lastname, date_of_birth, gender, blood_type, nationality, phone, email, emergency_contact, emergency_contact_relationship, created_by, last_updated_by FROM Patients";

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

// this allows the nurse/doctor to be taken to the medical records page, they each see a different once based on their roles access controls
    private void medicalRecords() {
        PatientData selectedPatient = tableView.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlertError("Selection Error", "Please select a patient before proceeding to view their medical records.");
            return;
        }

        String query = "SELECT role, firstname, lastname FROM Users WHERE id = ?";
        String userRole = null;
        String userFN = null;
        String userLN = null;

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, userID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                userRole = rs.getString("role");
                userFN = rs.getString("firstname");
                userLN = rs.getString("lastname");
            }

            DBUtils.closeConnection(con, statement);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to fetch user details.");
            return;
        }

        if (userRole == null) {
            showAlertError("Access Denied", "User role not found.");
            return;
        }

        String patientId = selectedPatient.getPatientId();

        if (userRole.equalsIgnoreCase("Doctor")) {
            medicalRecordsDoc medicalrecords = new medicalRecordsDoc(stage, patientId, userID, userFN, userLN);
            medicalrecords.initializeComponents();
        } else if (userRole.equalsIgnoreCase("Nurse")) {
            medicalRecordsNurse medicalrecords = new medicalRecordsNurse(stage, patientId, userID, userFN, userLN);
            medicalrecords.initializeComponents();
        } else {
            showAlertError("Access Denied", "You do not have permission to access medical records.");
        }
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
}


