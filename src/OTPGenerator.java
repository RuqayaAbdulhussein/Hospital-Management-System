import java.security.SecureRandom;

public class OTPGenerator {
    public static String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();  //use SecureRandom for better security
        int otp = 100000 + secureRandom.nextInt(900000); //generates a 6 digit otp
        return String.valueOf(otp);  //returns the otp as a string
    }
}
