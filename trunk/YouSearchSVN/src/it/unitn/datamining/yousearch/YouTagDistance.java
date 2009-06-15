package it.unitn.datamining.yousearch;

import java.io.Serializable;
import java.util.Enumeration;

import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.RevisionHandler;
import weka.core.neighboursearch.PerformanceStats;

public class YouTagDistance implements DistanceFunction, OptionHandler, Serializable, RevisionHandler   {
	
	private static final long serialVersionUID = 8237973477032068759L;
	protected Instances m_Data = null;
	
	public YouTagDistance(){
		
	}
	
	public double distance(Instance arg0, Instance arg1) {
		//System.out.println(arg0.toString() +" : "+ arg1.toString());
		
		System.out.println("Distance between: " +arg0.stringValue(1)+" (id:"+arg0.value(0)+")" );
		System.out.print(" and " +arg1.stringValue(1)+" (id:"+arg1.value(0)+")" );
		
		System.out.println(" distance :"+arg1.value(0));
		// TODO Auto-generated method stub
		return arg1.value(0);
	}

	public double distance(Instance arg0, Instance arg1, PerformanceStats arg2)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("distance 1");
		return 0;
	}

	public double distance(Instance arg0, Instance arg1, double arg2) {
		System.out.println("distance 2");
		// TODO Auto-generated method stub
		return 0;
	}

	public double distance(Instance arg0, Instance arg1, double arg2,
			PerformanceStats arg3) {
		// TODO Auto-generated method stub
		System.out.println("distance 3");
		return 0;
	}

	public String getAttributeIndices() {
		// TODO Auto-generated method stub
		System.out.println("getattrindices");
		return null;
	}

	public Instances getInstances() {
		// TODO Auto-generated method stub
		System.out.println("getinstances");
		return null;
	}

	public boolean getInvertSelection() {
		// TODO Auto-generated method stub
		System.out.println("invertselection");
		return false;
	}

	public void postProcessDistances(double[] arg0) {
		// TODO Auto-generated method stub
		System.out.println("postProcess");
	}

	public void setAttributeIndices(String arg0) {
		// TODO Auto-generated method stub
		System.out.println("setAttributeIndices");
		
	}

	public void setInstances(Instances arg0) {
		// TODO Auto-generated method stub
		System.out.println("setInstances");
	}

	public void setInvertSelection(boolean arg0) {
		// TODO Auto-generated method stub
		System.out.println("setInverted");
	}

	public void update(Instance arg0) {
		// TODO Auto-generated method stub
		System.out.println("update");
	}

	public String[] getOptions() {
		// TODO Auto-generated method stub
		System.out.println("getOptions");
		return null;
	}

	public Enumeration listOptions() {
		// TODO Auto-generated method stub
		System.out.println("listOptions");
		return null;
	}

	public void setOptions(String[] arg0) throws Exception {
		System.out.println("setOptions");
		// TODO Auto-generated method stub
		
	}

	public String getRevision() {
		// TODO Auto-generated method stub
		return null;
	}
}
