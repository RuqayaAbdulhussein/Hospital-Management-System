import java.util.HashMap;
import java.util.Map;
//this stores the email and otp that was sent to the user to then be checked for verifying
public class OTPStorage {
    private static final Map<String, String> otpMap = new HashMap<>();

    public static void storeOTP(String email, String otp) {
        otpMap.put(email, otp);
    }

    public static boolean verifyOTP(String email, String enteredOTP) {
        return otpMap.containsKey(email) && otpMap.get(email).equals(enteredOTP);
    }
}