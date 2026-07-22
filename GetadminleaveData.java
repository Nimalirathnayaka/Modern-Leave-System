import java.io.*;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/* Defines the servlet mapping URL so the admin dashboard script can fetch leave data from here */
@WebServlet("/GetAdminLeaveData")
public class GetAdminLeaveData extends HttpServlet {
    
    /* Handles HTTP GET requests to retrieve and generate HTML table rows for leave applications */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            /* Establishes connection to the database and creates a SQL statement object */
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            
            /* SQL query to fetch all leave applications sorted by ID in descending order (newest first) */
            String sql = "SELECT id, username, leave_type, start_date, end_date, status FROM leave_applications ORDER BY id DESC";
            ResultSet rs = stmt.executeQuery(sql);
            
            /* Loops through each leave record found in the database */
            while(rs.next()) {
                int id = rs.getInt("id");
                String status = rs.getString("status");
                
                /* Dynamically builds HTML table rows (tr) and cells (td) for each application */
                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("leave_type") + "</td>");
                out.println("<td>" + rs.getString("start_date") + "</td>");
                out.println("<td>" + rs.getString("end_date") + "</td>");
                
                /* Action cell: Displays Approve and Reject buttons if status is 'Pending', otherwise shows the final status */
                out.println("<td>");
                if ("Pending".equalsIgnoreCase(status)) {
                    /* Form sends update request to UpdateLeaveStatusServlet with leave ID and action type */
                    out.println("<form action='UpdateLeaveStatusServlet' method='POST' style='display:inline;'>" +
                                "<input type='hidden' name='id' value='" + id + "'>" +
                                "<button name='action' value='Approved' style='background:green; color:white; cursor:pointer;'>Approve</button> " +
                                "<button name='action' value='Rejected' style='background:red; color:white; cursor:pointer;'>Reject</button>" +
                                "</form>");
                } else {
                    /* If already approved or rejected, just displays the text status */
                    out.println(status);
                }
                out.println("</td>");
                out.println("</tr>");
            }
            /* Closes the database connection */
            conn.close();
            
        } catch(Exception e) { 
            /* Catches exceptions and outputs the error message inside the table row */
            e.printStackTrace(); 
            out.println("<tr><td colspan='6'>Error: " + e.getMessage() + "</td></tr>");
        }
    }
}