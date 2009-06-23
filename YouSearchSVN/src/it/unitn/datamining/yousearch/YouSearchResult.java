package it.unitn.datamining.yousearch;

import java.util.ArrayList;
	
public class YouSearchResult {
	private String keyword;
	private ArrayList<YouSearchEntry> listResult;
	
	public YouSearchResult(String keyword){
		this.keyword = keyword;
		this.listResult = new ArrayList<YouSearchEntry>();		
	}
	
	public boolean addItem(YouSearchEntry item){
		return this.listResult.add(item);		
	}
	
	public YouSearchEntry[] getResult(){
		return this.listResult.toArray(new YouSearchEntry[1]);
	}
	public int getSize(){
		if(this.listResult!=null)
			return this.listResult.size();
		
		return 0;
	}
	
	public YouSearchEntry getItem(int i){
		if(this.listResult!=null)
			return this.listResult.get(i);
		return null;
	}

	public String getKeyword(){
		return this.keyword;
	}
	
}
