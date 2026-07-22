import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* Defines the servlet mapping URL so the form can send data to this servlet */
@WebServlet("/ApplyLeaveServlet")
public class ApplyLeaveServlet extends HttpServlet {
    
    /* Handles HTTP POST requests sent when an employee submits a leave application form */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        /* Sets the response content type to HTML and initializes a writer to output script responses */
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        /* Retrieves the current user session to verify authentication */
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        
        /* Security check: If the user is not logged in, redirect them back to the login page */
        if (username == null) {
            response.sendRedirect("index.html");
            return;
        }

        /* Extracts leave form inputs submitted by the employee */
        String leaveType = request.getParameter("leave_type");
        String startDate = request.getParameter("start_date");
        String endDate = request.getParameter("end_date");
        String reason = request.getParameter("reason");
        
        /* Declares database connection and prepared statement objects */
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            /* Establishes connection to the database */
            conn = DatabaseConnection.getConnection();
            
            /* SQL query to insert the new leave application with a default status of 'Pending' */
            String sql = "INSERT INTO leave_applications (username, leave_type, start_date, end_date, reason, status) VALUES (?, ?, ?, ?, ?, 'Pending')";
            pstmt = conn.prepareStatement(sql);
            
            /* Binds form parameters and session username to the SQL query placeholders */
            pstmt.setString(1, username);
            pstmt.setString(2, leaveType);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            pstmt.setString(5, reason);
            
            /* Executes the update query and stores the number of affected rows */
            int rowsInserted = pstmt.executeUpdate();
            
            /* If the insert is successful, displays a success pop-up and redirects to the employee dashboard */
            if (rowsInserted > 0) {
                out.println("<script>alert('Leave Application Submitted Successfully!'); window.location.href='dashboard.jsp';</script>");
            }
        } catch (SQLException e) {
            /* Catches and displays any database errors */
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        } finally {
            /* Safely closes database statement and connection objects to prevent memory/resource leaks */
            try { 
                if (pstmt != null) pstmt.close(); 
                if (conn != null) conn.close(); 
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}