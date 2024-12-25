import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

@WebServlet("/signup")
public class Signup extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try{
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine())!= null){
                requestBody.append(line);
            }
            JSONObject json = new JSONObject(requestBody.toString());
            String userName = json.getString("userName");
            String email = json.getString("email");
            String password = json.getString("password");
            String security = json.getString("security");

            Connection con = SqlConnection.getConnection();
            String query = "INSERT INTO user (userName,email,password,security) VALUE ('"+userName+"','"+email+"','"+password+"','"+security+"')";
            Statement st = con.createStatement();
            System.out.println("conn");
            if(st.executeUpdate(query)>0){
                System.out.println("conn");
                HttpSession session = req.getSession();
                session.setAttribute("userName", userName);

                session.setMaxInactiveInterval(30 * 60);

                resp.setStatus(HttpServletResponse.SC_OK);
                JSONObject successResponse = new JSONObject();
                successResponse.put("message", "Login successful!");
                successResponse.put("userName", userName);
                successResponse.put("email",email);
                out.print(successResponse.toString());
            }else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("message", "Signup unsuccessfully");
                out.print(errorResponse.toString());
                return;
            }
        }catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            out.print(errorResponse.toString());
        }finally {
            out.close();
        }
    }
}
