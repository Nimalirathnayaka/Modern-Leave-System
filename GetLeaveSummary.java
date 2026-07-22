import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/* Defines the servlet mapping URL so the employee dashboard can fetch leave balance summary data from here */
@WebServlet("/GetLeaveSummary")
public class GetLeaveSummary extends HttpServlet {
    
    /* Handles HTTP GET requests to calculate and return leave statistics as a JSON response */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        /* Sets the response content type to JSON and character encoding to UTF-8 */
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        /* Retrieves the current active user session without creating a new one */
        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;
        
        /* Defines default total allowed leaves per year (e.g., 24 leaves) */
        int totalAllowed = 24; 
        int taken = 0;
        
        /* Checks if the user is logged in by verifying the username */
        if (username != null) {
            try {
                /* Establishes connection to the database */
                Connection conn = DatabaseConnection.getConnection();
                
                /* SQL query to count how many leave requests for this specific user have been 'Approved' */
                String sql = "SELECT COUNT(*) FROM leave_applications WHERE username = ? AND status = 'Approved'";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                
                /* Retrieves the count of taken leaves from the query result */
                if (rs.next()) {
                    taken = rs.getInt(1);
                }
                
                /* Closes the database connection */
                conn.close();
                
            } catch (Exception e) {
                /* Catches and prints any database or query exceptions */
                e.printStackTrace();
            }
        }
        
        /* Calculates remaining available leaves by subtracting taken leaves from total allowed leaves */
        int remaining = totalAllowed - taken;
        
        /* Formats the total, taken, and remaining leave counts into a JSON string */
        String jsonResponse = "{\"total\":" + totalAllowed + ", \"taken\":" + taken + ", \"remaining\":" + remaining + "}";
        
        /* Writes the JSON response back to the client/frontend dashboard */
        response.getWriter().write(jsonResponse);
    }
}