package it.unitn.datamining.yousearch;

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


public class YouSimilarity {
	private static YouSimilarity instance = null;
	private SimilarityMeasure measure = null;
	private boolean initialization = false;
	private Dictionary dict = null;

	public static YouSimilarity getInstance() {
		if (instance == null)
			instance = new YouSimilarity();
		return instance;
	}
	
	private void init() {
		try {
			JWNL.initialize(new FileInputStream("wn-config.xml"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JWNLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Create a map to hold the similarity config params
		Map<String, String> params = new HashMap<String, String>();

		// the simType parameter is the class name of the measure to use
		params.put("simType", "shef.nlp.wordnet.similarity.JCn");
		// this param should be the URL to an infocontent file (if required
		// by the similarity measure being loaded)
		params.put("infocontent", "file:ic-bnc-resnik-add1.dat");
		try {
			measure = SimilarityMeasure.newInstance(params);
			dict = Dictionary.getInstance();			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double getSimilarity(String word1, String word2) {

		double retSim = 0.0;
		IndexWord wordsense1 = null;
		IndexWord wordsense2 = null;		

		if (!initialization)
			init();
		
		initialization = true;
		
		try {
			
			wordsense1 = dict.getIndexWord(POS.NOUN, word1);
			wordsense2 = dict.getIndexWord(POS.NOUN, word2);
			if((wordsense1 != null) && (wordsense2 != null))
				retSim = measure.getSimilarity(wordsense1.getSense(1), wordsense2.getSense(1));
			
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retSim;
	}	
}