import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Defines the servlet mapping URL so the admin dashboard can send leave status update requests here */
@WebServlet("/UpdateLeaveStatusServlet")
public class UpdateLeaveStatusServlet extends HttpServlet {
    
    /* Handles HTTP POST requests sent when an admin approves or rejects a leave application */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        /* Retrieves the leave application ID and the chosen action (Approved/Rejected) from the request parameters */
        String leaveId = request.getParameter("id");
        String action = request.getParameter("action"); 
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            /* Establishes connection to the database */
            conn = DatabaseConnection.getConnection();
            
            /* SQL query to update the leave status of a specific record identified by its ID */
            String sql = "UPDATE leave_applications SET status = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, action);
            ps.setString(2, leaveId);
            
            /* Executes the update operation in the database */
            ps.executeUpdate();
            
        } catch (Exception e) {
            /* Catches and prints any database or execution exceptions */
            e.printStackTrace();
        } finally {
            /* Ensures database resources (PreparedStatement and Connection) are safely closed to prevent leaks */
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        /* Redirects the admin back to the leave requests view page after updating */
        response.sendRedirect("view_leaves.html");
    }
}