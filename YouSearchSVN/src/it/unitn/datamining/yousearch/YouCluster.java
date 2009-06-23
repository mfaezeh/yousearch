package it.unitn.datamining.yousearch;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.AddCluster;

public class YouCluster {
	private DataSource source;
	//private YouXMeans clusterAgent;
	private YouXMeans clusterAgent;	
	private YouTagDistance tagDistance;
	private Instances data;
	
	public YouCluster(String file, String[] options){
		clusterAgent = new YouXMeans();
		tagDistance = new YouTagDistance();
		try {
			source = new DataSource(file);
			clusterAgent.setDistanceF(tagDistance);
			data = source.getDataSet();
			clusterAgent.setMinNumClusters(2);
			clusterAgent.setMaxNumClusters(8);
			
			// actually, addClusterAssignment execute cluster
			data = this.addClusterAssignment(data);
			System.out.println(data.toString());
			//clusterAgent.buildClusterer(data);
			System.out.println(clusterAgent.toString());
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
	
	public static void main(String[] args){
		/* PROBLEMI NOTI: l'algoritmo XMeans modifica i volori durante il calcolo
		 * Durante lo splitCenter la funzione richiede valori double (potrebbe essere il valore di similitudine)
		 * Bisogna riscrivere la funzione splitCenter in modo che tenga conto anche dei tags
		 */
		String[] options=null;
		
		new YouCluster("conv_dump_movie.arff",options);
	}

}
