import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/* Defines the servlet mapping URL so the employee dashboard can fetch personal leave history from here */
@WebServlet("/GetLeaveData")
public class GetLeaveData extends HttpServlet {
    
    /* Handles HTTP GET requests to fetch and generate HTML table rows for the logged-in employee's leave records */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        /* Retrieves the current active user session without creating a new one */
        HttpSession session = request.getSession(false);

        /* Session security check: If user is not logged in, displays an error message inside the table */
        if (session == null || session.getAttribute("username") == null) {
            out.println("<tr><td colspan='4'>Please login first</td></tr>");
            return;
        }

        /* Extracts the logged-in employee's username from the session */
        String username = (String) session.getAttribute("username");

        try {
            /* Establishes connection to the database */
            Connection conn = DatabaseConnection.getConnection();
            
            /* SQL query to fetch all leave requests matching the logged-in username, newest first */
            String sql = "SELECT * FROM leave_applications WHERE username = ? ORDER BY id DESC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;
            
            /* Loops through each leave record found for this specific user */
            while(rs.next()){
                hasData = true;
                String status = rs.getString("status");
                
                /* Determines the CSS class for styling the status badge based on whether it's Approved, Rejected, or Pending */
                String statusClass = (status != null && status.equalsIgnoreCase("Approved")) ? "approved" : 
                                     (status != null && status.equalsIgnoreCase("Rejected")) ? "rejected" : "pending";

                /* Dynamically builds HTML table rows (tr) and data cells (td) */
                out.println("<tr>");
                out.println("<td>" + rs.getString("leave_type") + "</td>");
                out.println("<td>" + rs.getDate("start_date") + "</td>");
                out.println("<td>" + rs.getDate("end_date") + "</td>");
                
                /* Displays the status inside a styled HTML span badge */
                out.println("<td><span class='status-badge " + statusClass + "'>" + (status != null ? status : "Pending") + "</span></td>");
                out.println("</tr>");
            }
            
            /* If no leave requests exist for this user, displays a friendly message */
            if (!hasData) {
                out.println("<tr><td colspan='4'>No leave records found.</td></tr>");
            }
            
            /* Closes the database connection */
            conn.close();
            
        } catch(Exception e) { 
            /* Catches any database or connection errors and displays them inside the table row */
            out.println("<tr><td colspan='4'>Error: " + e.getMessage() + "</td></tr>");
            e.printStackTrace();
        }
    }
}