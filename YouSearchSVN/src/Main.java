import it.unitn.datamining.yousearch.YouCluster;


public class Main {
	public static void main(String[] args){
		String[] options=null;	
		
		for(int i = 0; i< args.length; i++)
			System.out.println(i+ ": " + args[i]);
		
		
		new YouCluster("conv_dump_sint_movie.arff",options);
	}
}
