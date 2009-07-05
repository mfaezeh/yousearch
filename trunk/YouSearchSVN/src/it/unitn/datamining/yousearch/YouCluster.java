package it.unitn.datamining.yousearch;

import java.io.ByteArrayInputStream;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.AddCluster;
import weka.clusterers.XMeans;

public class YouCluster {
	private DataSource source;
	private XMeans clusterAgent;
	//private YouXMeans clusterAgent;	
	private YouTagDistance tagDistance;
	private Instances data;
	private String keyword;
	private ByteArrayInputStream is;
	
	private void initCluster(){
		
		clusterAgent = new XMeans();
		tagDistance = new YouTagDistance();
		clusterAgent.setDistanceF(tagDistance);
		clusterAgent.setMinNumClusters(2);
		clusterAgent.setMaxNumClusters(8);
	}
	
	public YouCluster(String fileARFF, String keyword){
		
		this.initCluster();
		
		this.keyword = keyword;
		this.is = new ByteArrayInputStream(fileARFF.getBytes());
		try {
			source = new DataSource(this.is);			
			data = source.getDataSet();			
			// actually, addClusterAssignment execute cluster
			data = this.addClusterAssignment(data);
			System.out.println(data.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public YouCluster(String file, String[] options){
		
		this.initCluster();
		
		try {
			source = new DataSource(file);			
			data = source.getDataSet();			
			// actually, addClusterAssignment execute cluster
			data = this.addClusterAssignment(data);
			System.out.println(data.toString());
			//clusterAgent.buildClusterer(data);
			//System.out.println(clusterAgent.toString());
			System.out.println("My Centers");
			Instances centers = clusterAgent.getNearestFromCentroids();
			for(int i=0; i<centers.numInstances(); i++)
				System.out.println(centers.instance(i));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	 private Instances addClusterAssignment(Instances data) throws Exception {
			int totInstances = data.numInstances();
			Instances retVal = null;
			Instance processed = null;
			Instance current = null;
			AddCluster add = new AddCluster();
			
			this.clusterAgent.setUseKDTree(false);
			add.setClusterer(this.clusterAgent);
			//add.setIgnoredAttributeIndices("3-last");
			add.setInputFormat(data);
			

			for (int i = 0; i < totInstances; i++) {
				current = data.instance(i);
				add.input(current);
			}

			add.batchFinished();// filtra tutto in una volta
			retVal = add.getOutputFormat();
			while ((processed = add.output()) != null) {
				retVal.add(processed);
			}
			return retVal;
		}
	 
	public YouSearchResult getResult(){
		YouSearchResult retVal = new YouSearchResult(this.keyword);
		Instances centers = this.clusterAgent.getNearestFromCentroids();
		YouSearchEntry newEntry;
		for(int i = 0; i< centers.numInstances(); i++){
			newEntry = new YouSearchEntry();
			newEntry.setTitle("center_"+i);
			newEntry.setTags(centers.instance(i).stringValue(2));
			newEntry.setVideoId(centers.instance(i).stringValue(3));
			retVal.addItem(newEntry);			
		}		
		return retVal;
	}
	public YouSearchResult getResultByClusterPos(int pos){
		YouSearchResult retVal = new YouSearchResult(this.keyword);
		YouSearchEntry newEntry;
		for(int i = 0; i< this.data.numInstances(); i++){
			System.out.println(this.data.instance(i).stringValue(4));
			if(this.data.instance(i).stringValue(4).equalsIgnoreCase("cluster"+pos)){			
				newEntry = new YouSearchEntry();
				newEntry.setTitle("center_"+i);
				newEntry.setTags(this.data.instance(i).stringValue(2));
				newEntry.setVideoId(this.data.instance(i).stringValue(3));
				retVal.addItem(newEntry);			
			}	
		}
		return retVal;		
	}
	public static void main(String[] args){
		/* PROBLEMI NOTI: l'algoritmo XMeans modifica i volori durante il calcolo
		 * Durante lo splitCenter la funzione richiede valori double (potrebbe essere il valore di similitudine)
		 * Bisogna riscrivere la funzione splitCenter in modo che tenga conto anche dei tags
		 */
		String[] options=null;
		
		new YouCluster("conv_dump_sint_movie.arff",options);
	}

}
