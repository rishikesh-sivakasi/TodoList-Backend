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

@WebServlet("/fetchTask")
public class ReadTask extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        StringBuilder reqBody = new StringBuilder();
        String line ;
        while((line = req.getReader().readLine())!= null){
            reqBody.append(line);
        }
        JSONObject json = new JSONObject(reqBody.toString());

        String email = json.getString("email");
        String status = json.getString("status");

        try{
            System.out.println("try");
            Connection con = SqlConnection.getConnection();
            Statement st = con.createStatement();
            String query = "SELECT task_name , date FROM tasks WHERE email = '"+email+"' AND status = '"+status+"'";
            ResultSet rs = st.executeQuery(query);

            System.out.println("before read");
            StringBuilder resjson = new StringBuilder("[");
            while (rs.next()) {
                if (resjson.length() > 1)
                    resjson.append(",");
                resjson.append("{")
                        .append("\"task\":\"").append(rs.getString("task_name")).append("\",")
                        .append("\"date\":\"").append(rs.getString("date")).append("\"")
                        .append("}");
            }
            resjson.append("]");

            out.write(resjson.toString());
        }catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("msg","Internal server error");
            out.print(error.toString());
        }
    }
}
