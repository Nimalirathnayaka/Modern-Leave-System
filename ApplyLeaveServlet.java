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
@WebServlet("/ApplyLeaveServlet")
public class ApplyLeaveServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String leaveType = request.getParameter("leave_type");
        String startDate = request.getParameter("start_date");
        String endDate = request.getParameter("end_date");
        String reason = request.getParameter("reason");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO leave_applications (username, leave_type, start_date, end_date, reason) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, username);
            pstmt.setString(2, leaveType);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            pstmt.setString(5, reason);
            
            int rowsInserted = pstmt.executeUpdate();
            
            if (rowsInserted > 0) {
                out.println("<script type='text/javascript'>");
                out.println("alert('Leave Application Submitted Successfully! 🎉');");
                out.println("window.location.href='dashboard.html';");
                out.println("</script>");
            } else {
                out.println("<h3>Error occurred! Please try again.</h3>");
            }
            
        } catch (SQLException e) {
            out.println("<h3>Database Error: " + e.getMessage() + "</h3>");
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}