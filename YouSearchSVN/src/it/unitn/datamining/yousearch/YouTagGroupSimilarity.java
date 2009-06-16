package it.unitn.datamining.yousearch;

public class YouTagGroupSimilarity {
	
	private static double MAX_BOUND = 5.5;
	private static double NORMAL_FACTOR = 1 / MAX_BOUND;
	private YouSimilarity similarityAgent = null;
	private String keyword;
	
	public YouTagGroupSimilarity(String keyword){
		similarityAgent = YouSimilarity.getInstance();
		this.keyword = keyword;
	}
	
	double getSimilarityByPair(String first, String second){
		// optimization
		if((first != null) && (second != null) && (first.equalsIgnoreCase(second)))
			return 5.5;
		
		if((first != null) && (second != null))
			return similarityAgent.getSimilarity(first, second);
		else
			return 0.0;
	}
	
	double getSimilarity(String[] tags){

		double retSim = 0.0;
		//System.out.println(tags.length);
		
		//for(int i = 0; i < tags.length; i++)
			for(int j = 0; j < tags.length-1; j++)
					retSim += (this.getSimilarityByPair(keyword, tags[j] ));
					
		
		
		return retSim;
	}
}
