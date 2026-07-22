import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* Defines the servlet mapping URL so the login form can submit POST requests to this servlet */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    /* Handles HTTP POST requests sent when a user attempts to log in */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Extracts username and password values entered by the user in the login form */
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        /* Attempts to establish a connection to the MySQL database */
        Connection conn = DatabaseConnection.getConnection();
        
        /* Connection error check: If database connection fails, display an error message and exit */
        if (conn == null) {
            response.getWriter().println("Database connection failed. Please check if MySQL is running.");
            return;
        }
        
        try {
            /* SQL query to check if a user exists with the matching username and password */
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            /* Executes the query and stores the result set */
            ResultSet rs = pstmt.executeQuery();
            
            /* If user credentials are correct (record found in the database) */
            if (rs.next()) {
                String role = rs.getString("role"); 
                String fullName = rs.getString("username"); 
                
                /* Creates a new user session and stores user details for later use */
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("role", role);
                session.setAttribute("fullName", fullName); 
                
                /* Role-based redirection: Redirects to admin dashboard if role is admin, otherwise to employee dashboard */
                if ("admin".equalsIgnoreCase(role)) {
                    response.sendRedirect("admin_dashboard.html"); 
                } else {
                    response.sendRedirect("dashboard.jsp"); 
                }
            } else {
                /* If credentials are invalid, redirects back to the login page with an error parameter */
                response.sendRedirect(request.getContextPath() + "/index.html?error=1");
            }
            
            /* Closes the database connection */
            conn.close();
            
        } catch (Exception e) {
            /* Catches and prints any unexpected exceptions or database errors */
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}