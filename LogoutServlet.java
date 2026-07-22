import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* Defines the servlet mapping URL so when a user clicks logout, requests are directed here */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /* Handles HTTP GET requests triggered when logging out of the system */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        /* Retrieves the current active user session without creating a new one if it doesn't exist */
        HttpSession session = request.getSession(false);
        
        /* If an active session exists, destroys/invalidates it to clear stored user data */
        if (session != null) {
            session.invalidate(); 
        }
        
        /* Sets HTTP headers to prevent browser caching so users cannot use the back button to view pages after logging out */
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
        response.setHeader("Pragma", "no-cache"); 
        response.setDateHeader("Expires", 0); 
        
        /* Redirects the user back to the main login/index page */
        response.sendRedirect("index.html");
    }
}