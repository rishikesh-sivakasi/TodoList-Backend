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
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/forgetPassword")
public class ForgetPassword extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        StringBuilder requestString = new StringBuilder();
        String line;
        while((line = req.getReader().readLine())!=null){
            requestString.append(line);
        }
        JSONObject json = new JSONObject(requestString.toString());

        String email = json.getString("email");
        String security = json.getString("security");
        String newPassword = json.getString("newPassword");

        try{
            Connection con = SqlConnection.getConnection();
            Statement st = con.createStatement();

            String query = "SELECT * from user WHERE email = '"+email+"'";
            ResultSet rs = st.executeQuery(query);
            if(rs.next()){
                System.out.println(rs);
            }

            JSONObject errorResponse = new JSONObject();
            JSONObject successResponse = new JSONObject();

            if(!rs.getString("email").equals(email)){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorResponse.put("message", "Email is incorrect");
                out.print(errorResponse.toString());
                return;
            }else if(!rs.getString("security").equals(security)){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errorResponse.put("message", "Nick name is incorrect");
                out.print(errorResponse.toString());
                return;
            }

            String updateQuery = "UPDATE user SET password = '"+newPassword+"' WHERE email = '"+email+"'";

            if(st.executeUpdate(updateQuery)>0){
                resp.setStatus(HttpServletResponse.SC_OK);
                successResponse.put("msg","Password changed successfully");
                out.print(successResponse.toString());
            }
        }
        catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            out.print(errorResponse.toString());
        }
    }
}
