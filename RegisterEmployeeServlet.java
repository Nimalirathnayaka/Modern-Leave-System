import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/* Defines the servlet mapping URL so the employee registration form can submit POST requests here */
@WebServlet("/RegisterEmployeeServlet")
public class RegisterEmployeeServlet extends HttpServlet {
    
    /* Handles HTTP POST requests sent when registering a new user/employee */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /* Retrieves username, password, and role parameters submitted from the registration form */
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String role = request.getParameter("role");

        try {
            /* Establishes connection to the database */
            Connection conn = DatabaseConnection.getConnection();
            
            /* SQL query to insert new user records into the users table securely using PreparedStatement */
            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass); 
            ps.setString(3, role);
            
            /* Executes the database insert update */
            ps.executeUpdate();
            
            /* Closes the database connection */
            conn.close();
            
            /* Redirects the admin back to the manage employees page after successful registration */
            response.sendRedirect("manage_employees.html");
            
        } catch (Exception e) {
            /* Catches and prints any database or execution exceptions and outputs the error message */
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}