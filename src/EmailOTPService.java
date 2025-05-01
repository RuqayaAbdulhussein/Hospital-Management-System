import javax.mail.*;
import javax.mail.internet.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import io.github.cdimascio.dotenv.Dotenv;

public class EmailOTPService {
    //method to send an otp email to the specified recipient
    public static void sendEmail(String recipient, String otp) {
        Dotenv dotenv = Dotenv.load();
        final String senderEmail = dotenv.get("EMAIL_SENDER"); //senders email address
        final String senderPassword = fetchPasswordFromDB(); //fetch password from the database

        // Check if the password was fetched successfully
        if (senderPassword == null || senderPassword.isEmpty()) {
            System.out.println("Failed to fetch email password from database."); //handle error if password is not available
            return;
        }

        // Set up properties for email server (Gmail in this case)
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP host for gmail
        props.put("mail.smtp.port", "587"); // SMTP port for gmail
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS encryption

        //set up the email session with the sender's email and password
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword); //provide credentials for SMTP
            }
        });

        try {
            //create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail)); //set the sender's email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient)); //set the recipients email
            message.setSubject("Your OTP Code"); //set the subject of the email
            message.setText("Your OTP code is: " + otp); //set the body of the email with the otp code

            // Send the email
            Transport.send(message);
            System.out.println("OTP sent to email: " + recipient); //confirmation message
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch the sender's email password from the database
    private static String fetchPasswordFromDB() {
        String password = null;
        String query = "SELECT password FROM emailCommunication WHERE email = ?"; //parametarized query to fetch the password

        try (Connection con = DBUtils.establishConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, "roqaya430@gmail.com"); // Set the email for which the password is being fetched
            ResultSet rs = stmt.executeQuery();

            //ff the result set contains data then fetch the password
            if (rs.next()) {
                password = rs.getString("password"); //get the password from the database
            }
            DBUtils.closeConnection(con,stmt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }
}
