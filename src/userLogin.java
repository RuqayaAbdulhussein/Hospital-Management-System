import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//this is the main login page
public class userLogin {
    private Scene loginScene;
    private TextField userIDfield = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Stage stage;

    private static final int MaxFailedAttempts = 3;
    private static final int LockoutMins = 1;
    private int failedAttempts = 0;
    private long lastFailedAttempt = 0;

    public userLogin(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(10));

        Button loginButton = new Button("Sign In");

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                authenticate();
            }
        });

        loginLayout.getChildren().addAll(new Label("User ID:"), userIDfield,
                new Label("Password:"), passwordField , loginButton);

        loginScene = new Scene(loginLayout, 400, 200);
        stage.setTitle("Hospital Management System Log In");
        stage.setScene(loginScene);
        stage.show();
    }

    private void authenticate() {
        String userID = userIDfield.getText().trim();
        String password = passwordField.getText().trim();

        if (failedAttempts >= MaxFailedAttempts) { //if failed attempts is greater than the max (3), we implement lockout
            long timeElapsed = System.currentTimeMillis() - lastFailedAttempt; //how long has it been since the last failed attempt
            if (timeElapsed < LockoutMins * 60 * 1000) { //for the sake of testing and demo its only 1 minute
                showAlert("Account Locked", "Your account is locked. Please try again after " + (LockoutMins - timeElapsed / 60000) + " minutes.");
                return;
            } else {
                failedAttempts = 0;
            }
        }

        String userIDRegex = "^\\d{4}$"; //validation to ensure user ID is only 4 digits
        Pattern idPattern = Pattern.compile(userIDRegex);
        Matcher matchID = idPattern.matcher(userID);

        String passRegex = "^[a-zA-Z0-9@#%!*_\\-+=~.]*$";
        Pattern passPattern = Pattern.compile(passRegex);
        Matcher matchPass = passPattern.matcher(password);

        if (userID.isEmpty() || !matchID.matches()) {
            failedAttempts++;
            lastFailedAttempt = System.currentTimeMillis();
            showAlert("Validation Error", "User ID must be a 4-digit number.");
            return;
        }

        if (password.isEmpty() || !matchPass.matches()) {
            failedAttempts++;
            lastFailedAttempt = System.currentTimeMillis();
            showAlert("Validation Error", "Password field cannot be empty and must meet complexity requirements");
            return;
        }

        Connection con = DBUtils.establishConnection();
        String query = "SELECT password, salt FROM Users WHERE id=?";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String storedSalt = rs.getString("salt");
                byte[] hashedSalt = hexStringToByteArray(storedSalt);

                String hashedPassword = generateHash(password, hashedSalt);

                if (hashedPassword.equals(storedHashedPassword)) { //hash inputted pass and compare it with the one in the database
                    failedAttempts = 0;
                    enterOTP enterotp = new enterOTP(stage, userID);
                    enterotp.initializeComponents();
                } else {
                    failedAttempts++;
                    lastFailedAttempt = System.currentTimeMillis();
                    showAlert("Authentication Failed", "Invalid ID or password.");
                }
            } else {
                failedAttempts++;
                lastFailedAttempt = System.currentTimeMillis();
                showAlert("Authentication Failed", "User not found.");
            }

            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
