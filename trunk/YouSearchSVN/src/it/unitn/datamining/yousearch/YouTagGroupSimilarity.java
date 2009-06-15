package it.unitn.datamining.yousearch;

public class YouTagGroupSimilarity {
	
	private static double MAX_BOUND = 4;
	private static double NORMAL_FACTOR = 1 / MAX_BOUND;
	private YouSimilarity similarityAgent = null;
	
	public YouTagGroupSimilarity(){
		similarityAgent = YouSimilarity.getInstance();
	}
	
	double getSimilarityByPair(String first, String second){
		if((first != null) && (second != null))
			return similarityAgent.getSimilarity(first, second);
		else
			return 0.0;
	}
	
	double getSimilarity(String[] tags){
		// semplice calcolo della similitudine. 
		// Sommo tutte le similitudini e divido
		// per NORMAL_FACTOR
		
		// TROOOPPO LUNGOOO > 2 min per ricerca
		double retSim = 0.0;
		//System.out.println(tags.length);
		
		//for(int i = 0; i < tags.length; i++)
			for(int j = 0; j < tags.length-1; j++)
				//if(i!=j)
					retSim += (this.getSimilarityByPair(tags[j+1], tags[j] )) / NORMAL_FACTOR * tags.length;
					
		
		
		return retSim;
	}
}
