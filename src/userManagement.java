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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//this is the admin page in which they can manage all hospital staff user accounts.
public class userManagement {
    private Scene userManagementScene;
    private Stage stage;
    private TableView<UserData> tableView; //table to display the user data
    adminPanel adminpane = new adminPanel();
    private String userID;
    private String userFN;
    private String userLN;//stores the logged in users id

    public userManagement(Stage primaryStage, String userID, String userFN, String userLN){
        this.stage = primaryStage;
        this.userID = userID;
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


        topBar.getChildren().addAll(changePasswordButton, logoutButton); //the buttons at the top right

        Label titleLabel = new Label("This is the Admin Panel");


        tableView = new TableView<>();
        setupTableColumns(); // set up the columns for the data
        loadUserData(); //load the user data
        tableView.setOnMouseClicked(me -> updateUserView()); //upon clicking a row, update the view

        VBox adminForm = adminpane.getadminPanel(); //this is the admin form at the bottom for the admin to edit information

        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(10, 0, 0, 0));
        buttonPane.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        addButton.setOnAction(event -> {
            try {
                addUser();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                showAlertError("Error", "Password encryption failed. Please try again.");
            }
        });

        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logout();
            } //log out and go back to the login page
        });

        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changePassword(); //this will redirect them to the change password scene
            }
        });

        updateButton.setOnAction(event -> {
            updateUser(new ActionEvent()); //once clicked, validation will take place then if it passes the information will update.
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteUser(new ActionEvent()); //deleted a selected user
            }
        });

        buttonPane.getChildren().addAll(adminpane.clear, addButton, updateButton, deleteButton); //adds all those buttons to the button pane.

        layout.getChildren().addAll(topBar, titleLabel, tableView, adminForm, buttonPane); //adding all components to the layout

        userManagementScene = new Scene(layout, 800, 400);
        stage.setTitle("Admin Scene");
        stage.setScene(userManagementScene);
        stage.setMaximized(true);
        stage.show();
    }
// setting up cloumns for the table
    private void setupTableColumns() {
        TableColumn<UserData, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<UserData, Long> qidColumn = new TableColumn<>("QID");
        qidColumn.setCellValueFactory(cellData -> cellData.getValue().qidProperty().asObject());

        TableColumn<UserData, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<UserData, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<UserData, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<UserData, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<UserData, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<UserData, String> specializationColumn = new TableColumn<>("Specialization");
        specializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));

        TableColumn<UserData, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<UserData, String> createdByColumn = new TableColumn<>("Created By: ");
        createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

        TableColumn<UserData, String> updatedByColumn = new TableColumn<>("Last Updated By: ");
        updatedByColumn.setCellValueFactory(new PropertyValueFactory<>("updatedBy"));
// add columns to table view
        tableView.getColumns().addAll(idColumn, qidColumn, firstNameColumn, lastNameColumn, emailColumn, phoneColumn, roleColumn, specializationColumn, departmentColumn, createdByColumn, updatedByColumn);
    }
//method to load user data into the table
    private void loadUserData() {
        ObservableList<UserData> userList = FXCollections.observableArrayList(); //list to store user data
        Connection con = DBUtils.establishConnection();
        String query = "SELECT id, QID, firstname, lastname, email, phone, role, specialization, department, created_by, last_updated_by FROM users";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet rs = statement.executeQuery(); //executes query and fets results

            while (rs.next()) { //extract user data from the result set and add it to the list
                int id = rs.getInt("id");
                Long QID = rs.getLong("qid");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String role = rs.getString("role");
                String specialization = rs.getString("specialization");
                String department = rs.getString("department");
                String createdBy = rs.getString("created_by");
                String updatedBy = rs.getString("last_updated_by");

                userList.add(new UserData(id, QID, firstName, lastName, email, phone, role, specialization, department, createdBy, updatedBy));
            }

            tableView.setItems(userList); //set the data in the table view
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to connect to the database.");
        }
    }
    //method to update the user view based on the selected user in the table
    private void updateUserView() {
        UserData selectedUser = tableView.getSelectionModel().getSelectedItem(); // Get the selected user from the table
        if (selectedUser != null) {
            //populate the fields in the admin pane with the selected users information
            adminpane.QID.setText(String.valueOf(selectedUser.getQID()));
            adminpane.QID.setDisable(true);
            adminpane.firstname.setText(selectedUser.getFirstName());
            adminpane.lastname.setText(selectedUser.getLastName());
            adminpane.email.setText(selectedUser.getEmail());
            adminpane.phone.setText(selectedUser.getPhone());
            adminpane.role.setValue(selectedUser.getRole());
            adminpane.specialization.setValue(selectedUser.getSpecialization());
            adminpane.department.setValue(selectedUser.getDepartment());

            updateSpecandDepVisibility(selectedUser.getRole()); //update the visibility of specialization and department fields based on user role

        }
    }

    private void updateSpecandDepVisibility(String role) {
        if (role != null && role.equals("Doctor")) { //only if selected role is doctor, we make the specialization and department fields visible
            adminpane.specialization.setVisible(true);
            adminpane.department.setVisible(true);
            adminpane.specLabel.setVisible(true);
            adminpane.depLabel.setVisible(true);
        } else {
            adminpane.specialization.setVisible(false);
            adminpane.department.setVisible(false);
            adminpane.specLabel.setVisible(false);
            adminpane.depLabel.setVisible(false);
        }
    }

    private void addUser() throws NoSuchAlgorithmException {
        if (!Validation()){ //checks the admin pane fields for nullity and against regular expressions
            return;
        }

        String QID = adminpane.QID.getText().trim();
        String firstName = adminpane.firstname.getText().trim();
        String lastName = adminpane.lastname.getText().trim();
        String email = adminpane.email.getText().trim();
        String phone = adminpane.phone.getText().trim();

        if (!checkDuplicateID(QID)){ //ensures the user isn't adding the same user again by verifying the QID
            return; //if QID is duplicate stop the process
        }

        String rawPassword = firstName.toLowerCase() + "." + lastName.toLowerCase(); //password is created by default for the new user with their first and last name.

        byte[] salt = createSalt();
        String saltString = bytesToStringHex(salt);

        String hashedPassword = generateHash(rawPassword, salt); //hash the default password before storing it into the database along with the salt
//inserting into the database using parameterized queries
        String adminName = userFN + " " + userLN;
        String sql = "INSERT INTO Users (QID, firstname, lastname, email, phone, role, specialization, department, salt, password, created_by, last_updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, QID);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, email);
            statement.setString(5, phone);
            statement.setString(6, adminpane.getSelectedRole());
            statement.setString(7, adminpane.getSelectedSpec());
            statement.setString(8, adminpane.getSelectedDep());
            statement.setString(9, saltString);
            statement.setString(10, hashedPassword);
            statement.setString(11, adminName);
            statement.setString(12, null);

            statement.executeUpdate();
            clearFields(); //clears the admin pane fields after the insert
            loadUserData(); //reload the user data with the new addition
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String generateHash(String newPassword, byte[] salt) throws NoSuchAlgorithmException {
        String algorithm = "SHA-256";
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(salt);
        byte[] hash = digest.digest(newPassword.getBytes());
        return bytesToStringHex(hash);

    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToStringHex(byte[] bytes){
        char[] hexChars = new char[bytes.length * 2];
        for (int j =0; j<bytes.length; j++){
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] createSalt(){
        byte[] bytes = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    // this method updates a users information
    private void updateUser(ActionEvent event) {
        if (!Validation()){ //ensure the input in the admin pane pass the input validation
            return;
        }

        UserData selectedUser = tableView.getSelectionModel().getSelectedItem(); //get the selected user
        String adminName = userFN + " " + userLN;

        if (selectedUser != null) {
            Alert confirmDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION); //verifies if the user would like to go through with this update.
            confirmDeleteAlert.setTitle("Confirm Update");
            confirmDeleteAlert.setHeaderText("Are you sure you want to update this user?");

            confirmDeleteAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String sql = "UPDATE Users SET firstname = ?, lastname = ?, email = ?, phone = ?, role = ?, specialization = ?, department = ?, last_updated_by = ? WHERE id = ?";

                    try (Connection con = DBUtils.establishConnection();
                         PreparedStatement statement = con.prepareStatement(sql)) {
                        statement.setString(1, adminpane.firstname.getText());
                        statement.setString(2, adminpane.lastname.getText());
                        statement.setString(3, adminpane.email.getText());
                        statement.setString(4, adminpane.phone.getText());
                        statement.setString(5, adminpane.getSelectedRole());
                        statement.setString(6, adminpane.specialization.getValue());
                        statement.setString(7, adminpane.department.getValue());
                        statement.setString(8, adminName);
                        statement.setInt(9, selectedUser.getId());

                        statement.executeUpdate();
                        clearFields();
                        loadUserData();

                        showAlertConfirm("Success", "User updated successfully.");

                        DBUtils.closeConnection(con, statement);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlertError("Error", "Failed to update the user.");
                    }
                }
            });
        } else {
            showAlertError("Error", "No user selected to be updated.");
        }
    }

// this method deletes a user
    private void deleteUser(ActionEvent event) {
        UserData selectedUser = tableView.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            Alert confirmDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDeleteAlert.setTitle("Confirm Deletion");
            confirmDeleteAlert.setHeaderText("Are you sure you want to delete this user?");
            confirmDeleteAlert.setContentText("This action cannot be undone.");

            confirmDeleteAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String sql = "DELETE FROM Users WHERE id = ?";

                    try (Connection con = DBUtils.establishConnection();
                         PreparedStatement statement = con.prepareStatement(sql)) {
                        statement.setInt(1, selectedUser.getId());
                        statement.executeUpdate();
                        clearFields();
                        loadUserData();
                        showAlertConfirm("Success", "User deleted successfully.");
                        DBUtils.closeConnection(con, statement);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlertError("Error", "Failed to delete the user.");
                    }
                }
            });
        } else {
            showAlertError("Error", "No user selected for deletion.");
        }
    }

    //input validation function
    private boolean Validation() {
        String QID = adminpane.QID.getText().trim();
        String firstName = adminpane.firstname.getText().trim();
        String lastName = adminpane.lastname.getText().trim();
        String email = adminpane.email.getText().trim();
        String phone = adminpane.phone.getText().trim();
        if (adminpane.getSelectedRole() == null || adminpane.getSelectedRole().isEmpty()) { //based on the drop-down selection, ensure its not empty
            showAlertError("Validation Error", "Role cannot be empty.");
            return false;
        }
        if (adminpane.getSelectedRole().equals("Doctor")) {
            if (adminpane.getSelectedSpec() == null || adminpane.getSelectedSpec().isEmpty()) {
                showAlertError("Validation Error", "Doctor specialization cannot be empty.");
                return false;
            }

            if (adminpane.getSelectedDep() == null || adminpane.getSelectedDep().isEmpty()) {
                showAlertError("Validation Error", "Doctor department cannot be empty.");
                return false;
            }
        }
// regular expressions the text fields must match before proceeding
        String QIDRegex = "^\\d{11}$";
        String nameRegex = "^[a-zA-Z. ]{3,50}$";
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
        return true;
    }

//ensure no duplicate entries for users based on their QID
    private boolean checkDuplicateID(String QID) {
        String sql = "SELECT COUNT(*) FROM Users WHERE QID = ?";
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
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to check for duplicate QID.");
        }
        return true;
    }

    //this method clears the admin pane fields after adding, updating or deleting
    private void clearFields() {
        adminpane.QID.setDisable(false);
        adminpane.QID.clear();
        adminpane.firstname.clear();
        adminpane.lastname.clear();
        adminpane.role.setValue(null);
        adminpane.email.clear();
        adminpane.phone.clear();
        adminpane.specialization.setValue(null);
        adminpane.department.setValue(null);
        adminpane.specialization.setVisible(false);
        adminpane.department.setVisible(false);
        adminpane.specLabel.setVisible(false);
        adminpane.depLabel.setVisible(false);
    }

//takes the user back to the login page
    private void logout(){
        userLogin login = new userLogin(stage);
        login.initializeComponents();
    }
//tales the user to the change password page
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


