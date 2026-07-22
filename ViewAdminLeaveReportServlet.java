import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/* Defines the servlet mapping URL so admins can access the comprehensive leave report page */
@WebServlet("/ViewAdminLeaveReportServlet")
public class ViewAdminLeaveReportServlet extends HttpServlet {
    
    /* Handles HTTP GET requests to generate a printable HTML report containing all employee leave records */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        /* Defines custom CSS styling for the report layout, tables, and print media rules */
        String css = "<style>" +
            "body { font-family: sans-serif; margin: 40px; }" +
            "table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
            "th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }" +
            "th { background-color: #2c3e50; color: white; }" +
            ".no-print { margin-top: 20px; }" +
            ".btn { padding: 10px 20px; background: #28a745; color: white; border: none; cursor: pointer; text-decoration: none; margin-right:10px; }" +
            /* Media query hides navigation and print buttons automatically when printing/saving as PDF */
            "@media print { .no-print { display: none; } }" +
            "</style>";

        /* Writes the HTML header and stylesheet into the response */
        out.println("<html><head>" + css + "</head><body>");
        out.println("<h1>All Leave Report</h1>");
        out.println("<table><tr><th>ID</th><th>Employee Name</th><th>Leave Type</th><th>From</th><th>To</th><th>Status</th></tr>");

        try {
            /* Establishes connection to the database and executes a query to fetch all leave records */
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM leave_applications");
            
            /* Initializes a sequential counter to display row numbers starting from 1 */
            int rowCount = 1; 
            
            /* Loops through each leave record in the result set */
            while(rs.next()){
                /* Uses the sequential rowCount instead of the database ID column for clean numbering */
                out.println("<tr><td>" + rowCount + "</td>" + 
                            "<td>" + rs.getString("username") + "</td>" +
                            "<td>" + rs.getString("leave_type") + "</td>" +
                            "<td>" + rs.getString("start_date") + "</td>" +
                            "<td>" + rs.getString("end_date") + "</td>" +
                            "<td>" + rs.getString("status") + "</td></tr>");
                
                /* Increments the counter for the next table row */
                rowCount++; 
            }
            /* Closes the database connection */
            conn.close();
        } catch (Exception e) { 
            /* Catches and displays any database or SQL exceptions inside the table */
            out.println("<tr><td colspan='6'>Error: " + e.getMessage() + "</td></tr>"); 
        }

        /* Closes the table structure */
        out.println("</table>");
        
        /* Adds action buttons (Back navigation and Print/PDF generation button) wrapped in a non-printable container */
        out.println("<div class='no-print'><a href='admin_dashboard.html' class='btn' style='background:#6c757d;'>Back</a> " +
                    "<button onclick='window.print()' class='btn'>Print/PDF Report</button></div>");
        out.println("</body></html>");
    }
}