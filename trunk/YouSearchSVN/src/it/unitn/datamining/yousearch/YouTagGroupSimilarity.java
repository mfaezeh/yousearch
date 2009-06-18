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
	
	YouSimilarityDTO getSimilarity(String[] tags, String videoId){

		YouSimilarityDTO retSim = new YouSimilarityDTO();
		retSim.videoId = videoId;
			for(int j = 0; j < tags.length; j++){
					double temp =0.0;
					temp = (this.getSimilarityByPair(keyword, tags[j] ));
					retSim.sum += temp;
					System.out.println(tags[j]+ " " + temp);
			}
		retSim.similarity = retSim.sum / tags.length;
		System.out.println("groupInfo:\n sum: "+retSim.sum+"\n sim: "+retSim.similarity);			
		System.out.println("----------------------------");
		return retSim;
	}
}

class YouSimilarityDTO{
	public double similarity = 0.0;
	public double sum = 0.0;
	public String videoId = "";
}
