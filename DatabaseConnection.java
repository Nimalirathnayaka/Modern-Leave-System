import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // XAMPP MySQL එකේ Default විස්තර
    private static final String URL = "jdbc:mysql://localhost:3006/modern_leave_system";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP වල සාමාන්‍යයෙන් Password එකක් නැත (හිස් කොටුවකි)

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // MySQL Driver එක Load කිරීම
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connection එක සාදාගැනීම
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database Connection Successful! 🎉");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver එක හොයාගන්න බැහැ: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database එකට සම්බන්ධ වෙන්න බැරි වුණා: " + e.getMessage());
        }
        return connection;
    }
}