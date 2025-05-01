import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class changePassword {
    private Scene changePasswordScene;
    private PasswordField oldPasswordField = new PasswordField();
    private PasswordField newPasswordField = new PasswordField();
    private Stage stage;
    private String userID;
    private String userFN;
    private String userLN;//stores the logged-in users id
    private Label statusLabel;

    public changePassword(Stage primaryStage, String userID, String userFN, String userLN){
        this.stage = primaryStage;
        this.userID = userID;
        this.userFN =userFN;
        this.userLN =userLN;

    }

    public void initializeComponents()  {
        VBox changePasswordLayout = new VBox(10);
        changePasswordLayout.setPadding(new Insets(10));
        Button changePasswordButton = new Button("Change Password");
        Button backButton = new Button("Back");
        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    changePassword();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    showAlertError("Error", "Password encryption failed. Please try again.");
                }
            }
        });

        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                backUserManage();
            }
        });

        changePasswordLayout.getChildren().addAll(
                new Label("Welcome "),
                new Label("Old Password: "), oldPasswordField,
                new Label("New Password:"), newPasswordField,
                changePasswordButton,
                backButton,
                new Label("Upon clicking this button, you will be logged out and must log back in with your new password")
        );

        changePasswordScene = new Scene(changePasswordLayout, 550, 270);
        stage.setTitle("Change Password");
        stage.setScene(changePasswordScene);
        stage.show();
    }

    private void changePassword() throws NoSuchAlgorithmException {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty()) { //ensures both fields are filled
            showAlertError("Input Error", "Both fields must be filled.");
            return;
        }

        Connection con = DBUtils.establishConnection();
        String query = "SELECT password, salt FROM Users WHERE id=?";

        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, userID);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                //regular expression to ensure new password entered meets compelxity requirments
                String passRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#%!*_\\-+=~]).{8,}$";

                Pattern passPattern = Pattern.compile(passRegex);

                Matcher matchnewPass = passPattern.matcher(newPassword);
                if (matchnewPass.matches()){
                String storedHashedPassword = rs.getString("password");
                String storedSalt = rs.getString("salt");


                String hashedOldPassword = generateHash(oldPassword, "SHA-256", hexStringToByteArray(storedSalt));

                if (storedHashedPassword.equals(hashedOldPassword)) { //ensures old password is correct
                    byte[] newSalt = createSalt();
                    String newSaltString = bytesToStringHex(newSalt);
                    String hashedNewPassword = generateHash(newPassword, "SHA-256", newSalt);

                    String updateQuery = "UPDATE Users SET password=?, salt=? WHERE id=?";
                    try (PreparedStatement updateStatement = con.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, hashedNewPassword);
                        updateStatement.setString(2, newSaltString);
                        updateStatement.setString(3, userID);

                        int result = updateStatement.executeUpdate();
                        if (result == 1) {
                            showAlertConfirm("Success", "Password successfully changed.");
                            userLogin logIn = new userLogin(stage);
                            logIn.initializeComponents();
                        } else {
                            showAlertError("Failure", "Failed to update password.");
                        }
                    }
                } else {
                    showAlertError("Error", "Old password is incorrect.");
                }
            } else{
                    showAlertError("Error", "Password must be at least 8 characters, with a lower and upper case character and at least 1 special character");
                }
            DBUtils.closeConnection(con, preparedStatement);
        }} catch (Exception e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to connect to the database.");
        }}

    private static String generateHash(String password, String algorithm, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(salt);
        byte[] hash = digest.digest(password.getBytes());
        return bytesToStringHex(hash);
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToStringHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] createSalt() {
        byte[] bytes = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    private void backUserManage(){
        String query = "SELECT role, firstname, lastname FROM Users WHERE id = ?";
        String role = null;
        String userFN = null;
        String userLN = null;

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, userID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                role = rs.getString("role");
                userFN = rs.getString("firstname");
                userLN = rs.getString("lastname");
            }
            DBUtils.closeConnection(con, statement);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlertError("Database Error", "Failed to fetch user details.");
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
