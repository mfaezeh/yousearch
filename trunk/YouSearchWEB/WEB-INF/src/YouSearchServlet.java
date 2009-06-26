
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.util.*;
import com.google.gdata.client.Query.CategoryFilter;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Category;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.util.ServiceException;

import it.unitn.datamining.yousearch.YouCluster;
import it.unitn.datamining.yousearch.YouDTO2ARFF;
import it.unitn.datamining.yousearch.YouSearch;
import it.unitn.datamining.yousearch.YouSearchResult;
import it.unitn.datamining.yousearch.YouSearchEntry;

public class YouSearchServlet extends HttpServlet{

	private YouSearch finder;
	private YouCluster cluster;
	private YouDTO2ARFF converter;
	private boolean alreadyDump = false;
	private PrintWriter out;
	private String remotePath;
	private String keyword;
	private String clusterDetail;
	
    public void doGet ( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException    {
    	
    	System.out.println("INSTANCE: "+this);
    	
    	this.remotePath = this.getServletContext().getRealPath("/");
    	
    	YouSearchResult ret;
    	
    	
        response.setContentType("text/html");
        this.out = response.getWriter();

        String[] path = request.getRequestURI().split("(/)");
        
        
        if(path.length<4)
        	return;        
       
        this.keyword = path[3];
       
        /*intercetto il parametro che identifica il cluster da analizzare*/
        	this.clusterDetail = null;
        if(this.keyword.indexOf("=")!=-1)
        	this.clusterDetail = this.keyword.substring(this.keyword.indexOf("=")+1);
        
        String htmlOutput = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">";
        
        if(this.clusterDetail!=null){
        	this.keyword = this.keyword.substring(0,this.keyword.indexOf("="));
        	ret = this.doSearchDetail();
        }else{
        	ret = this.doSearch(keyword);
        }
    	
    	if(ret != null)
    		for(int t=0;t < ret.getSize() && (t<150); t++)
    			htmlOutput+= "<tr><td>"+this.builtVideoHtml(ret.getItem(t),t)+"</td></tr>";
    	else
    		htmlOutput += this.emptyResult();
    	
		htmlOutput += "</table>";
			//System.out.println(ret.getItem(t).getVideoId()+ " >> "+ret.getItem(t).getTags());
		
		out.print(htmlOutput);
		System.out.println(System.currentTimeMillis());
    	
    }	
	private String emptyResult(){
		return "<tr><td>No Result Found</td></tr>";
	}
    private String builtVideoHtml(YouSearchEntry entry,int count){
    	YouTubeService service = this.finder.getYouTubeService();
    	
    	if(entry.getVideoId()==null)
    		return "";
    	
    	String videoEntryUrl = "http://gdata.youtube.com/feeds/api/videos/"+entry.getVideoId();
    	//System.out.println(videoEntryUrl);
    	VideoEntry videoEntry;
    	String retHtml = "";
		try {
			videoEntry = service.getEntry(new URL(videoEntryUrl), VideoEntry.class);
			//out.println("titolo: "+videoEntry.getMediaGroup().getTitle().getPlainTextContent());

			String counter = "<b>"+(count+1)+"</b>";
			String title = "<p class=\"video_title\">"+videoEntry.getMediaGroup().getTitle().getPlainTextContent();
			title +="</p>";
			
			String img = "<p class=\"video_thumbs\">";			
			for(int i = 0; i<videoEntry.getMediaGroup().getThumbnails().size() && i<3; i++)
				img +=" <img class=\"video_thumb\" src=\""+videoEntry.getMediaGroup().getThumbnails().get(i).getUrl()+"\" border=\"0\">";
			img +="</p>";
			
			String tags = "<p class=\"video_tags\">";
			String[] tgsArray = entry.getTags().split(" ");
			for(int j = 0; j<tgsArray.length && j < 10;j++)
				tags += "<a class=\"video_tag\" href=\"http://www.youtube.com/results?search_query="+tgsArray[j]+"\" title=\""+tgsArray[j]+"\" target=\"_blank\">"+tgsArray[j]+"</a>&nbsp;&nbsp;";
			tags +="</p>";
			
			String inside = "<span class=\"video_inside\">";
				inside +="<a href=\"/YouSearch/cluster/"+this.keyword+"="+((count+1))+"\"><img src=\"/YouSearch/images/more-movie.png\" width=\"28\" border=\"0\" />See inside this cluster</a>";
				inside +="</span>"; 
				
			retHtml = counter + title + img + tags + inside;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    	return retHtml;
    }
	
	private YouSearchResult doSearch(String keyword){
		
		System.out.println(System.currentTimeMillis());
		this.finder = YouSearch.getInstance();
		this.finder.search(keyword, false);
		this.converter = new YouDTO2ARFF(finder.getResult());
		this.cluster = new YouCluster(this.converter.getARFF(),keyword);
		this.alreadyDump = true;
		return this.cluster.getResult();
	}
	private YouSearchResult doSearchDetail(){
		// se non ho fatto prima la query completa la devo rieseguire
		// se ho già fatto la query devo controllare che sia sulla stessa keyword
		if(!this.alreadyDump)
			this.doSearch(this.keyword);
		else
			if(!this.keyword.equalsIgnoreCase(this.cluster.getResult().getKeyword()))
				this.doSearch(this.keyword);
		
		//System.out.println(System.currentTimeMillis());
		//1-based
		if(this.clusterDetail!=null)
			return this.cluster.getResultByClusterPos(Integer.valueOf(this.clusterDetail).intValue());
		
		return this.cluster.getResult();
	}
	
}
