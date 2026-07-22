import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Defines the servlet mapping URL so the manage employees page can fetch user records from here */
@WebServlet("/GetEmployeesServlet")
public class GetEmployeesServlet extends HttpServlet {
    
    /* Handles HTTP GET requests to retrieve user data and generate HTML table rows */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            /* Establishes connection to the database and creates a SQL statement object */
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            
            /* SQL query to fetch ID, username, and role of all registered users from the users table */
            ResultSet rs = stmt.executeQuery("SELECT id, username, role FROM users");
            
            /* Loops through each user record found in the database result set */
            while(rs.next()) {
                /* Dynamically builds HTML table rows (tr) and data cells (td) containing user details */
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("role") + "</td>");
                out.println("</tr>");
            }
            
            /* Closes the database connection */
            conn.close();
            
        } catch(Exception e) { 
            /* Catches any exceptions and prints the stack trace along with an error message */
            e.printStackTrace(); 
            out.println("Error: " + e.getMessage());
        }
    }
}