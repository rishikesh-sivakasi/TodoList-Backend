import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {

    private static Connection con = null;

    private SqlConnection() {}

    public static Connection getConnection() {
        if (con == null) {
            synchronized (SqlConnection.class) {
                if (con == null) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/todo","root","Rishi@94860");
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Database connection failed.", e);
                    }
                }
            }
        }
        return con;
    }
}
