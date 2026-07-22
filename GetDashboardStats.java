import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement; 
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Defines the servlet mapping URL so the frontend dashboard can fetch statistical data from here */
@WebServlet("/GetDashboardStats")
public class GetDashboardStats extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /* Handles HTTP GET requests to gather metrics and return them as JSON data */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        /* Sets the response type to JSON and text encoding to UTF-8 */
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        /* Automatically opens and closes the database connection safely using try-with-resources */
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            int totalEmployees = 0;
            int totalRequests = 0;
            int respondedRequests = 0;

            /* Query 1: Counts the total number of regular employees in the users table */
            try (PreparedStatement ps1 = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'employee'");
                 ResultSet rs1 = ps1.executeQuery()) {
                if (rs1.next()) totalEmployees = rs1.getInt(1);
            }

            /* Query 2: Counts the total number of leave applications submitted */
            try (PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM leave_applications");
                 ResultSet rs2 = ps2.executeQuery()) {
                if (rs2.next()) totalRequests = rs2.getInt(1);
            }

            /* Query 3: Counts how many leave requests have been responded to (status is not 'Pending') */
            try (PreparedStatement ps3 = conn.prepareStatement("SELECT COUNT(*) FROM leave_applications WHERE status != 'Pending'");
                 ResultSet rs3 = ps3.executeQuery()) {
                if (rs3.next()) respondedRequests = rs3.getInt(1);
            }

            /* Formats the collected counter numbers into a JSON text structure */
            String json = String.format(
                "{\"totalEmployees\": %d, \"totalRequests\": %d, \"respondedRequests\": %d}", 
                totalEmployees, totalRequests, respondedRequests
            );

            /* Sends the JSON string back to the frontend dashboard script */
            out.print(json);
            out.flush();
            
        } catch (Exception e) {
            /* Catches any errors, prints trace, and returns an internal server error status with JSON error message */
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}