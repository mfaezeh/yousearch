package it.unitn.datamining.yousearch;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import weka.core.stemmers.SnowballStemmer;

public class YouXML2ARFF {
	private FileWriter fw = null;
	private String inputFile = "";
	private String outputFile = "";
	private String keyword = "";
	private YouTagGroupSimilarity similarityAgent = null;
	private SnowballStemmer stemmer = null;
	private YouTagValidator tagValidator = null; 

	private void doConversion() {
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(this.inputFile);
			this.keyword = doc.getElementsByTagName("result").item(0).getAttributes().item(1).getTextContent();
			
			System.out.println(this.keyword);
			//System.exit(1);
			this.similarityAgent = new YouTagGroupSimilarity(this.keyword);
			//this.stemmer = new SnowballStemmer();
			this.tagValidator = new YouTagValidator();
			NodeList items = doc.getElementsByTagName("video");			
			// iterations over videos
			for (int i = 0; i < items.getLength(); i++) {

				String itemId = items.item(i).getAttributes().getNamedItem("id").getNodeValue();
				String tag = "";
				NodeList itemInfo = items.item(i).getChildNodes();
				NodeList tags = null;
				ArrayList<String> arrayTag;
				YouSimilarityDTO similarityDTO = new YouSimilarityDTO();

				// iterations over video info (title,tags)
				for (int j = 0; j < itemInfo.getLength(); j++) {
					if (itemInfo.item(j).getNodeName() == "keywords") {
						tags = itemInfo.item(j).getChildNodes();
						arrayTag = new ArrayList<String>();
						similarityDTO.similarity = 0.0;
						// iterations over tags
						for (int t = 0; t < tags.getLength(); t++)
							if (tags.item(t).getNodeName() == "key"){
								tag = tags.item(t).getFirstChild().getNodeValue();
								// check wheater the tag is valid
								
								if(this.tagValidator.isValid(tag))
									//arrayTag.add(this.stemmer.stem(tag));
									// this stemmer is unuseful
									arrayTag.add(tag);
							}

						if (arrayTag.size() != 0) {
							similarityDTO = this.similarityAgent.getSimilarity(arrayTag.toArray(new String[0]),itemId);
						}
						//System.out.println(arrayTag.size());
						appendToFile(arrayTag, similarityDTO);
					}

				}

			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void appendToFile(ArrayList<String> tags, YouSimilarityDTO value) {
/*
  			fw.write("@attribute similarity numeric\n");	
			fw.write("@attribute tags string\n\n");
			fw.write("@attribute video_id string\n");
 */
		if (this.fw == null)
			return;

		String toFile = "";

		for (int i = 0; i < tags.size(); i++)
			toFile += tags.get(i).toString() + " ";
		try {
			fw.write("\n"+String.valueOf(value.similarity) + "," + String.valueOf(value.sum) + "," + "\"" + toFile.trim() + "\"" + "," +"\""+String.valueOf(value.videoId)+"\"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void initARFF(){
		if(this.fw == null)
			return;
		
		try {
			fw.write("@relation tags\n\n");
			fw.write("@attribute similarity numeric\n");
			fw.write("@attribute sum numeric\n");				
			fw.write("@attribute tags string\n");
			fw.write("@attribute video_id string\n\n");			
			fw.write("@data\n\n");				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public YouXML2ARFF(String input, String output) {
		try {
			this.inputFile = input;
			this.outputFile = output;
			fw = new FileWriter(this.outputFile);
			initARFF();
			doConversion();
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new YouXML2ARFF("dump_movie", "conv_dump_movie.arff");
	}

}
