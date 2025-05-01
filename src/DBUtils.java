import java.sql.Connection;
import java.sql.DriverManager;
import io.github.cdimascio.dotenv.Dotenv;

public class DBUtils {
    private static final Dotenv dotenv = Dotenv.load();

    public static Connection establishConnection() {
        try {
            String url = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String pass = dotenv.get("DB_PASS");
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection con, AutoCloseable... resources) {
        try {
            for (AutoCloseable res : resources) {
                if (res != null) res.close();
            }
            if (con != null) con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
