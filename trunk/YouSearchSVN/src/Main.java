import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;

import shef.nlp.wordnet.similarity.SimilarityMeasure;


public class Main {

		public static void main(String[] args){	
				try {
					JWNL.initialize(new FileInputStream("wn-config.xml"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JWNLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			//Create a map to hold the similarity config params
			Map<String,String> params = new HashMap<String,String>();
			  
			//the simType parameter is the class name of the measure to use
			params.put("simType","shef.nlp.wordnet.similarity.JCn");
			//this param should be the URL to an infocontent file (if required
			//by the similarity measure being loaded)
			params.put("infocontent","file:ic-bnc-resnik-add1.dat");			
			try {
				SimilarityMeasure simJCn = SimilarityMeasure.newInstance(params);
				//Get two words from WordNet
				Dictionary dict = Dictionary.getInstance();		
				IndexWord word1 = dict.getIndexWord(POS.NOUN,"asdas");
				IndexWord word2 = dict.getIndexWord(POS.NOUN,"adfff");
				
				System.out.println(word2.getSense(1));
				System.out.println(word2.getSense(1).toString());
				
				double sim = simJCn.getSimilarity(word1.getSense(1), word2.getSense(1));
				System.out.println(sim);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Ciao Mondo");
		}
}
