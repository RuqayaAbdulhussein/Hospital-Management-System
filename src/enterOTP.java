import java.sql.*;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//upon entering correct login credentials, users will be brought here to verify their identity once again using an otp then get redirected to the correct page according to their role.
public class enterOTP {
    private Scene loginScene;
    private TextField otpField = new TextField();
    private Stage stage;
    private String userID;
    private Label statusLabel;

    private int failedAttempts = 0;
    private long lockTime = 0;

    public enterOTP(Stage primaryStage, String userID){
        this.stage = primaryStage;
        this.userID = userID;
    }

    public void initializeComponents() {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(10));
        statusLabel = new Label();

        Button sendOtpButton = new Button("Send OTP");
        Button verifyOtpButton = new Button("Continue");

        sendOtpButton.setOnAction(e -> sendOTP());
        verifyOtpButton.setOnAction(e -> verifyOTP());

        loginLayout.getChildren().addAll(new Label("Enter OTP: "), otpField, sendOtpButton, verifyOtpButton);

        loginScene = new Scene(loginLayout, 400, 200);
        stage.setTitle("2-step verification");
        stage.setScene(loginScene);
        stage.show();
    }

    private void sendOTP() {
        Connection con = DBUtils.establishConnection();
        String query = "SELECT email, last_otp_time FROM Users WHERE id=?";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userID);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");
                Timestamp lastOtpTime = rs.getTimestamp("last_otp_time"); //when was the last time this user requested an otp.

                if (lastOtpTime != null) {
                    long elapsedTime = System.currentTimeMillis() - lastOtpTime.getTime();
                    if (elapsedTime < 60000) { //if the last time this user requested an otp was less than a minute ago, prompt them to wait
                        showAlert("Wait", "Please wait 1 minute before requesting another OTP.");
                        return;
                    }
                }

                String otp = OTPGenerator.generateOTP();
                OTPStorage.storeOTP(email, otp);
                EmailOTPService.sendEmail(email, otp);

                String updateQuery = "UPDATE Users SET last_otp_time = ? WHERE id = ?";
                PreparedStatement updateStatement = con.prepareStatement(updateQuery);
                updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis())); //update the last_otp_time to now
                updateStatement.setString(2, userID);
                updateStatement.executeUpdate();

                statusLabel.setText("OTP sent to " + email);
            } else {
                statusLabel.setText("User not found.");
            }

            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
        }
    }

    private void verifyOTP() {
        Connection con = DBUtils.establishConnection();
        String query = "SELECT email, role, last_otp_time FROM Users WHERE id=?";

        String enteredOTP = otpField.getText();

        String otpRegex = "^[0-9]{6}$"; //input validation to ensure otp entered is 6 digits.
        if (!enteredOTP.matches(otpRegex)) {
            showAlert("Invalid OTP", "OTP must be a 6-digit number.");
            failedAttempts++;
            if (failedAttempts >= 3) {
                lockTime = System.currentTimeMillis();
                showAlert("Locked Out", "Too many failed attempts. Try again in 1 minute.");
            } else {
                showAlert("Incorrect OTP", "Wrong OTP. Attempts left: " + (3 - failedAttempts));
            }
            return;
        }

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String role = rs.getString("role");
                Timestamp lastOtpTime = rs.getTimestamp("last_otp_time");

                if (lockTime != 0) {
                    long lockDuration = System.currentTimeMillis() - lockTime;
                    if (lockDuration < 60000) { //if its been less than 60 seconds since the user got locked they will be prompted to wait
                        showAlert("Locked Out", "Too many failed attempts. Try again in 1 minute.");
                        return;
                    } else {
                        resetFailedAttempts(); //failed attempts reset after 60 seconds and they can try again
                    }
                }

                if (lastOtpTime == null || (System.currentTimeMillis() - lastOtpTime.getTime()) > 60000) { //if its been longer than 60 seconds since the last otp, the user must request a new one.
                    showAlert("Expired OTP", "Your OTP has expired. Please request a new one.");
                    return;
                }

                if (OTPStorage.verifyOTP(email, enteredOTP)) { // verify email and OTP
                    resetFailedAttempts();

                    // Expire password
                    String updateQuery = "UPDATE Users SET last_otp_time = ? WHERE id = ?";
                    PreparedStatement updateStatement = con.prepareStatement(updateQuery);
                    updateStatement.setTimestamp(1, null); // mark password as expired
                    updateStatement.setString(2, userID);
                    updateStatement.executeUpdate();
                    updateStatement.close();

                    statusLabel.setText("OTP Verified. Login Successful!");
                    redirectUser(role, userID);
                }
                else {
                    failedAttempts++;
                    if (failedAttempts >= 3) {
                        lockTime = System.currentTimeMillis();
                        showAlert("Locked Out", "Too many failed attempts. Try again in 1 minute.");
                    } else {
                        showAlert("Incorrect OTP", "Wrong OTP. Attempts left: " + (3 - failedAttempts));
                    }
                }
            } else {
                showAlert("Authentication Failed", "Invalid ID or password.");
            }

            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
        }
    }

    private void resetFailedAttempts() {
        failedAttempts = 0;
    }

//this function will redirect the user to the correct page based on their role (RBAC)
    private void redirectUser(String role, String userID){
        String query = "SELECT firstname, lastname FROM Users WHERE id = ?";
        String userFN = null;
        String userLN = null;

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, userID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                userFN = rs.getString("firstname");
                userLN = rs.getString("lastname");
            }
            DBUtils.closeConnection(con, statement);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to fetch user details.");
            return;
        }

        if (role.equals("Admin")){
            userManagement usermanage = new userManagement(stage, userID, userFN, userLN);
            usermanage.initializeComponents();
        }else if(role.equals("Receptionist")){
            patientRegistration patientreg = new patientRegistration(stage, userID, userFN, userLN);
            patientreg.initializeComponents();
        }else if(role.equals("Doctor") || role.equals("Nurse")){
            patientRecordsDocNurse patientrecordsdoc = new patientRecordsDocNurse(stage, userID, userFN, userLN);
            patientrecordsdoc.initializeComponents();
        }else{
            statusLabel.setText("You do not have permissions to access this system.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
