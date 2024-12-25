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

@WebServlet("/deleteTask")
public class DeleteTask extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        StringBuilder requestString = new StringBuilder();
        String line;
        while((line = req.getReader().readLine()) != null){
            requestString.append(line);
        }
        JSONObject json = new JSONObject(requestString.toString());

        String task = json.getString("task");
        String email =json.getString("email");

        try{
            System.out.println("try delete");
            Connection con = SqlConnection.getConnection();
            Statement st = con.createStatement();
            String query = "DELETE FROM tasks WHERE email = '"+email+"' AND task_name = '"+task+"'";

            JSONObject response = new JSONObject();
            System.out.println("before");
            if(st.executeUpdate(query)>0){
                resp.setStatus(HttpServletResponse.SC_OK);
                response.put("msg","Deleted successfully");
                out.print(response.toString());
            }else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.put("msg","Something went wrong");
                out.print(response.toString());
            }
        }catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("msg","Internal server error");
            out.print(error.toString());
        }
    }
}
