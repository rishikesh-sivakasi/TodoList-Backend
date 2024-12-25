import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.*;

@WebServlet("/login")
public class Login extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try{
            StringBuilder requestBody = new StringBuilder();
            String line ;
            while ((line = req.getReader().readLine()) != null){
                requestBody.append(line);
            }

            JSONObject jsonRequest = new JSONObject(requestBody.toString());
            String email = jsonRequest.getString("email");
            String password = jsonRequest.getString("password");

            Connection con = SqlConnection.getConnection();
            Statement st = con.createStatement();
            String query = "SELECT * FROM user WHERE email = '"+email+"'";
            ResultSet rs = st.executeQuery(query);
            System.out.println("result set" + rs);
            if(rs.next()){
                System.out.println(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4));
            }

            boolean isValid;
            if(rs.getString(3).equals(email)){
                isValid = true;
            }else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("message", "Email is incorrect");
                out.print(errorResponse.toString());
                return;
            }

            if(rs.getString(4).equals(password)){
                isValid = true;
            }else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("message", "Password is incorrect");
                out.print(errorResponse.toString());
                return;
            }

            if(isValid){
                HttpSession session = req.getSession();
                session.setAttribute("userName", rs.getString(2));

                session.setMaxInactiveInterval(2*30);

                resp.setStatus(HttpServletResponse.SC_OK);
                JSONObject successResponse = new JSONObject();
                successResponse.put("message", "Login successful!");
                successResponse.put("userName", rs.getString(2));
                successResponse.put("email",rs.getString("email"));
                out.print(successResponse.toString());
            }
        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            out.print(errorResponse.toString());
        } finally {
            out.close();
        }
    }
}
