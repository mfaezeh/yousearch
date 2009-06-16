package it.unitn.datamining.yousearch;
/*
 *  -I <num>
  maximum number of overall iterations
  (default 1).

 -M <num>
  maximum number of iterations in the kMeans loop in
  the Improve-Parameter part 
  (default 1000).

 -J <num>
  maximum number of iterations in the kMeans loop
  for the splitted centroids in the Improve-Structure part 
  (default 1000).

 -L <num>
  minimum number of clusters
  (default 2).

 -H <num>
  maximum number of clusters
  (default 4).

 -B <value>
  distance value for binary attributes
  (default 1.0).

 -use-kdtree
  Uses the KDTree internally
  (default no).

 -K <KDTree class specification>
  Full class name of KDTree class to use, followed
  by scheme options.
  eg: "weka.core.neighboursearch.kdtrees.KDTree -P"
  (default no KDTree class used).

 -C <value>
  cutoff factor, takes the given percentage of the splitted 
  centroids if none of the children win
  (default 0.0).

 -D <distance function class specification>
  Full class name of Distance function class to use, followed
  by scheme options.
  (default weka.core.EuclideanDistance).

 -N <file name>
  file to read starting centers from (ARFF format).

 -O <file name>
  file to write centers to (ARFF format).

 -U <int>
  The debug level.
  (default 0)

 -Y <file name>
  The debug vectors file.

 -S <num>
  Random number seed.
  (default 10)
 */

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class YouCluster {
	private DataSource source;
	private YouXMeans clusterAgent;
	private YouTagDistance tagDistance;
	private Instances data;
	
	public YouCluster(String file, String[] options){
		clusterAgent = new YouXMeans();
		tagDistance = new YouTagDistance();
		try {
			source = new DataSource(file);
			clusterAgent.setDistanceF(tagDistance);
			//clusterAgent.setOptions(options);
			data = source.getDataSet();
			clusterAgent.buildClusterer(this.data);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println(clusterAgent.toString());
	}
	
	 private Instances transformAttributesFromStringToWordArray(Instances data) throws Exception {
			int totInstances = data.numInstances();
			Instances retVal = null;
			Instance processed = null;
			Instance current = null;
			StringToWordVector strToArray = new StringToWordVector();

			strToArray.setInputFormat(data);

			for (int i = 0; i < totInstances; i++) {
				current = data.instance(i);
				strToArray.input(current);
				strToArray.input(current);
			}

			strToArray.batchFinished();// filtra tutto in una volta
			retVal = strToArray.getOutputFormat();
			while ((processed = strToArray.output()) != null) {
				retVal.add(processed);
			}
			return retVal;
		}	
	
	public static void main(String[] args){
		/* PROBLEMI NOTI: l'algoritmo XMeans modifica i volori durante il calcolo
		 * Durante lo splitCenter la funzione richiede valori double (potrebbe essere il valore di similitudine)
		 * Bisogna riscrivere la funzione splitCenter in modo che tenga conto anche dei tags
		 * 
		 */
		String[] options;
		
		if(args.length < 1){
			options = new String[6];
			options[0]="-I";
			options[1]="3";
			options[2]="-L";
			options[3]="2";
			options[4]="-H";
			options[5]="10";
		}else{
			options = args;
		}
		new YouCluster("conv_dump_movie.arff",options);
	}

}