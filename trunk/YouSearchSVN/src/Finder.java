import it.unitn.datamining.yousearch.YouSearch;


public class Finder {
	private static final String PROXY_URL  = "proxy.science.unitn.it";
	private static final String PROXY_PORT = "3128";
	
	public static void main(String[] args){
		//setProxy();
		YouSearch finder = new YouSearch("movie");
	}

	public static void setProxy(){
		
		System.getProperties().put( "proxySet", "true" );
		System.getProperties().put( "proxyHost", PROXY_URL);
		System.getProperties().put( "proxyPort", PROXY_PORT);
		
		
	}
}
