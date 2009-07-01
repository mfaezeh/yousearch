import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class YouClusterServlet extends HttpServlet {

        public void doGet ( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException    {

                response.setContentType("text/html");
                PrintWriter out = response.getWriter();

                String[] path = request.getRequestURI().split("(/)");
                
                
                if(path.length<4)
                	return;
                
                String keyword = path[3];
                String server = request.getServerName()+":8080";
                
                
                
                //System.out.println(request.getServerName());
                
                String htmlPage = this.readFileAsString(this.getServletContext().getRealPath("/")+"/template/main.tpl");
                htmlPage = htmlPage.replaceAll("KEYWORD",keyword);
                htmlPage = htmlPage.replaceAll("SERVER",server);
                out.println(htmlPage);
                                
        }// end doGet
        
        private String readFileAsString(String filePath)
        throws java.io.IOException{
            StringBuffer fileData = new StringBuffer(1000);
            BufferedReader reader = new BufferedReader(
                    new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            return fileData.toString();
        }
        

}