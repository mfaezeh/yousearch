/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    YouXMeans.java
 *    Copyright (C) 2000 University of Waikato, Hamilton, New Zealand
 *
 */

package it.unitn.datamining.yousearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

import weka.clusterers.RandomizableClusterer;
import weka.clusterers.XMeans;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.neighboursearch.KDTree;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 * <!-- globalinfo-start --> Cluster data using the YouX-means algorithm.<br/>
 * <br/>
 * YouX-Means is K-Means extended by an Improve-Structure part In this part of the
 * algorithm the centers are attempted to be split in its region. The decision
 * between the children of each center and itself is done comparing the
 * BIC-values of the two structures.<br/>
 * <br/>
 * For more information see:<br/>
 * <br/>
 * Dan Pelleg, Andrew W. Moore: YouX-means: Extending K-means with Efficient
 * Estimation of the Number of Clusters. In: Seventeenth International
 * Conference on Machine Learning, 727-734, 2000.
 * <p/>
 * <!-- globalinfo-end -->
 * 
 * <!-- technical-bibteYouX-start --> BibTeX:
 * 
 * <pre>
 * &#064;inproceedings{Pelleg2000,
 *    author = {Dan Pelleg and Andrew W. Moore},
 *    booktitle = {Seventeenth International Conference on Machine Learning},
 *    pages = {727-734},
 *    publisher = {Morgan Kaufmann},
 *    title = {YouX-means: Extending K-means with Efficient Estimation of the Number of Clusters},
 *    year = {2000}
 * }
 * </pre>
 * <p/>
 * <!-- technical-bibteYouX-end -->
 * 
 * <!-- options-start --> Valid options are:
 * <p/>
 * 
 * <pre>
 * -I &lt;num&gt;
 *  maximum number of overall iterations
 *  (default 1).
 * </pre>
 * 
 * <pre>
 * -M &lt;num&gt;
 *  maximum number of iterations in the kMeans loop in
 *  the Improve-Parameter part 
 *  (default 1000).
 * </pre>
 * 
 * <pre>
 * -J &lt;num&gt;
 *  maximum number of iterations in the kMeans loop
 *  for the splitted centroids in the Improve-Structure part 
 *  (default 1000).
 * </pre>
 * 
 * <pre>
 * -L &lt;num&gt;
 *  minimum number of clusters
 *  (default 2).
 * </pre>
 * 
 * <pre>
 * -H &lt;num&gt;
 *  maximum number of clusters
 *  (default 4).
 * </pre>
 * 
 * <pre>
 * -B &lt;value&gt;
 *  distance value for binary attributes
 *  (default 1.0).
 * </pre>
 * 
 * <pre>
 * -use-kdtree
 *  Uses the KDTree internally
 *  (default no).
 * </pre>
 * 
 * <pre>
 * -K &lt;KDTree class specification&gt;
 *  Full class name of KDTree class to use, followed
 *  by scheme options.
 *  eg: &quot;weka.core.neighboursearch.kdtrees.KDTree -P&quot;
 *  (default no KDTree class used).
 * </pre>
 * 
 * <pre>
 * -C &lt;value&gt;
 *  cutoff factor, takes the given percentage of the splitted 
 *  centroids if none of the children win
 *  (default 0.0).
 * </pre>
 * 
 * <pre>
 * -D &lt;distance function class specification&gt;
 *  Full class name of Distance function class to use, followed
 *  by scheme options.
 *  (default weka.core.EuclideanDistance).
 * </pre>
 * 
 * <pre>
 * -N &lt;file name&gt;
 *  file to read starting centers from (ARFF format).
 * </pre>
 * 
 * <pre>
 * -O &lt;file name&gt;
 *  file to write centers to (ARFF format).
 * </pre>
 * 
 * <pre>
 * -U &lt;int&gt;
 *  The debug level.
 *  (default 0)
 * </pre>
 * 
 * <pre>
 * -Y &lt;file name&gt;
 *  The debug vectors file.
 * </pre>
 * 
 * <pre>
 * -S &lt;num&gt;
 *  Random number seed.
 *  (default 10)
 * </pre>
 * 
 * <!-- options-end -->
 * 
 * @author Gabi Schmidberger (gabi@cs.waikato.ac.nz)
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author Malcolm Ware (mfw4@cs.waikato.ac.nz)
 * @version $Revision: 1.24 $
 * @see RandomizableClusterer
 */
public class YouXMeans extends XMeans implements
		TechnicalInformationHandler {

	/*
	 * major TODOS:
	 * 
	 * make BIC-Score replaceable by other scores
	 */

	/** for serialization. */
	private static final long serialVersionUID = -7941793078404132616L;

	
	/**
	 * the default constructor.
	 */
	public YouXMeans() {
		super();
		m_SeedDefault = 10;
		setSeed(m_SeedDefault);
	}

	/**
	 * Returns a string describing this clusterer.
	 * 
	 * @return a description of the evaluator suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Cluster data using the YouX-means algorithm.\n\n"
				+ "YouX-Means is K-Means extended by an Improve-Structure part In this "
				+ "part of the algorithm the centers are attempted to be split in "
				+ "its region. The decision between the children of each center and "
				+ "itself is done comparing the BIC-values of the two structures.\n\n"
				+ "For more information see:\n\n"
				+ getTechnicalInformation().toString();
	}

	/**
	 * Returns an instance of a TechnicalInformation object, containing detailed
	 * information about the technical background of this class, e.g., paper
	 * reference or book this class is based on.
	 * 
	 * @return the technical information about this class
	 */
	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result;

		result = new TechnicalInformation(Type.INPROCEEDINGS);
		result.setValue(Field.AUTHOR, "Dan Pelleg and Andrew W. Moore");
		result
				.setValue(
						Field.TITLE,
						"YouX-means: Extending K-means with Efficient Estimation of the Number of Clusters");
		result.setValue(Field.BOOKTITLE,
				"Seventeenth International Conference on Machine Learning");
		result.setValue(Field.YEAR, "2000");
		result.setValue(Field.PAGES, "727-734");
		result.setValue(Field.PUBLISHER, "Morgan Kaufmann");

		return result;
	}

	/**
	 * Generates the YouX-Means clusterer.
	 * 
	 * @param data
	 *            set of instances serving as training data
	 * @throws Exception
	 *             if the clusterer has not been generated successfully
	 */
	public void buildClusterer(Instances data) throws Exception {

		// can clusterer handle the data?
		// getCapabilities().testWithFail(data);

		//System.out.println("FIRST: " + data.instance(2).stringValue(3));

		m_NumSplits = 0;
		m_NumSplitsDone = 0;
		m_NumSplitsStillDone = 0;

		// replace missing values
		m_ReplaceMissingFilter = new ReplaceMissingValues();
		m_ReplaceMissingFilter.setInputFormat(data);
		// m_Instances = Filter.useFilter(data, m_ReplaceMissingFilter);
		m_Instances = data;
		// initialize random function
		Random random0 = new Random(m_Seed);

		// num of clusters to start with
		m_NumClusters = m_MinNumClusters;

		// set distance function to default
		if (m_DistanceF == null) {
			m_DistanceF = new EuclideanDistance();
		}

		m_DistanceF.setInstances(m_Instances);
		checkInstances();

		if (m_DebugVectorsFile.exists() && m_DebugVectorsFile.isFile())
			initDebugVectorsInput();

		// make list of indexes for m_Instances
		int[] allInstList = new int[m_Instances.numInstances()];
		for (int i = 0; i < m_Instances.numInstances(); i++) {
			allInstList[i] = i;
		}

		// set model used (just for convenience)
		m_Model = new Instances(m_Instances, 0);

		// produce the starting centers
		if (m_CenterInput != null) {
			// read centers from file
			m_ClusterCenters = new Instances(m_CenterInput);
			m_NumClusters = m_ClusterCenters.numInstances();
		} else
			// makes the first centers randomly
			m_ClusterCenters = makeCentersRandomly(random0, m_Instances,
					m_NumClusters);
		PFD(D_FOLLOWSPLIT, "\n*** Starting centers ");
		for (int k = 0; k < m_ClusterCenters.numInstances(); k++) {
			PFD(D_FOLLOWSPLIT, "Center " + k + ": "
					+ m_ClusterCenters.instance(k));
		}

		PrCentersFD(D_PRINTCENTERS);

		boolean finished = false;
		Instances children;

		if (m_UseKDTree)
			m_KDTree.setInstances(m_Instances);

		// loop counter of main loop
		m_IterationCount = 0;

		/**
		 * "finished" does get true as soon as: 1. number of clusters gets >=
		 * m_MaxClusters, 2. in the last round, none of the centers have been
		 * split
		 * 
		 * if number of clusters is already >= m_MaxClusters part 1 (=
		 * Improve-Params) is done at least once.
		 */
		while (!finished && !stopIteration(m_IterationCount, m_MaxIterations)) {

			/*
			 * ==================================================================
			 * == 1. Improve-Params conventional K-means
			 */

			PFD(D_FOLLOWSPLIT, "\nBeginning of main loop - centers:");
			PrCentersFD(D_FOLLOWSPLIT);

			PFD(D_ITERCOUNT, "\n*** 1. Improve-Params " + m_IterationCount
					+ ". time");
			m_IterationCount++;

			// prepare to converge
			boolean converged = false;

			// initialize assignments to -1
			m_ClusterAssignments = initAssignments(m_Instances.numInstances());
			// stores a list of indexes of instances belonging to each center
			int[][] instOfCent = new int[m_ClusterCenters.numInstances()][];

			// KMeans loop counter
			int kMeansIteration = 0;

			// converge in conventional K-means
			// ----------------------------------
			PFD(D_FOLLOWSPLIT, "\nConverge in K-Means:");
			while (!converged
					&& !stopKMeansIteration(kMeansIteration, m_MaxKMeans)) {

				kMeansIteration++;
				converged = true;

				// assign instances to centers
				// -------------------------------------
				converged = assignToCenters(m_UseKDTree ? m_KDTree : null,
						m_ClusterCenters, instOfCent, allInstList,
						m_ClusterAssignments, kMeansIteration);

				PFD(D_FOLLOWSPLIT, "\nMain loop - Assign - centers:");
				PrCentersFD(D_FOLLOWSPLIT);
				// compute new centers = centers of mass of points
				converged = recomputeCenters(m_ClusterCenters, // clusters
						instOfCent, // their instances
						m_Model); // model information
				PFD(D_FOLLOWSPLIT, "\nMain loop - Recompute - centers:");
				PrCentersFD(D_FOLLOWSPLIT);
			}
			PFD(D_FOLLOWSPLIT, "");
			PFD(D_FOLLOWSPLIT,
					"End of Part: 1. Improve-Params - conventional K-means");

			/**
			 * ================================================================
			 * ===== 2. Improve-Structur
			 */

			// BIC before split distortioning the centres
			m_Mle = distortion(instOfCent, m_ClusterCenters);
			m_Bic = calculateBIC(instOfCent, m_ClusterCenters, m_Mle);
			PFD(D_FOLLOWSPLIT, "m_Bic " + m_Bic);

			int currNumCent = m_ClusterCenters.numInstances();
			Instances splitCenters = new Instances(m_ClusterCenters,
					currNumCent * 2);

			// store BIC values of parent and children
			double[] pbic = new double[currNumCent];
			double[] cbic = new double[currNumCent];

			// split each center
			for (int i = 0; i < currNumCent
			// this could help to optimize the algorithm
			// && currNumCent + numSplits <= m_MaxNumClusters
			; i++) {

				PFD(D_FOLLOWSPLIT, "\nsplit center " + i + " "
						+ m_ClusterCenters.instance(i));
				Instance currCenter = m_ClusterCenters.instance(i);
				int[] currInstList = instOfCent[i];
				int currNumInst = instOfCent[i].length;

				// not enough instances; than continue with next
				if (currNumInst <= 2) {
					pbic[i] = Double.MAX_VALUE;
					cbic[i] = 0.0;
					// add center itself as dummy
					splitCenters.add(currCenter);
					splitCenters.add(currCenter);
					continue;
				}

				// split centers ----------------------------------------------
				double variance = m_Mle[i] / (double) currNumInst;
				children = splitCenter(random0, currCenter, variance, m_Model);

				// initialize assignments to -1
				int[] oneCentAssignments = initAssignments(currNumInst);
				int[][] instOfChCent = new int[2][]; // todo maybe split didn't
														// work

				// converge the children --------------------------------------
				converged = false;
				int kMeansForChildrenIteration = 0;
				PFD(D_FOLLOWSPLIT, "\nConverge, K-Means for children: " + i);
				while (!converged
						&& !stopKMeansIteration(kMeansForChildrenIteration,
								m_MaxKMeansForChildren)) {
					kMeansForChildrenIteration++;

					converged = assignToCenters(children, instOfChCent,
							currInstList, oneCentAssignments);

					if (!converged) {
						recomputeCentersFast(children, instOfChCent, m_Model);
					}
				}

				// store new centers for later decision if they are taken
				splitCenters.add(children.instance(0));
				splitCenters.add(children.instance(1));

				PFD(D_FOLLOWSPLIT, "\nconverged cildren ");
				PFD(D_FOLLOWSPLIT, " " + children.instance(0));
				PFD(D_FOLLOWSPLIT, " " + children.instance(1));

				// compare parent and children model by their BIC-value
				pbic[i] = calculateBIC(currInstList, currCenter, m_Mle[i],
						m_Model);
				double[] chMLE = distortion(instOfChCent, children);
				cbic[i] = calculateBIC(instOfChCent, children, chMLE);

			} // end of loop over clusters

			// decide which one to split and make new list of cluster centers
			Instances newClusterCenters = null;
			newClusterCenters = newCentersAfterSplit(pbic, cbic,
					m_CutOffFactor, splitCenters);
			/**
			 * Compare with before Improve-Structure
			 */
			int newNumClusters = newClusterCenters.numInstances();
			if (newNumClusters != m_NumClusters) {

				PFD(D_FOLLOWSPLIT, "Compare with non-split");

				// initialize assignments to -1
				int[] newClusterAssignments = initAssignments(m_Instances
						.numInstances());

				// stores a list of indexes of instances belonging to each
				// center
				int[][] newInstOfCent = new int[newClusterCenters
						.numInstances()][];

				// assign instances to centers
				// -------------------------------------
				converged = assignToCenters(m_UseKDTree ? m_KDTree : null,
						newClusterCenters, newInstOfCent, allInstList,
						newClusterAssignments, m_IterationCount);

				double[] newMle = distortion(newInstOfCent, newClusterCenters);
				double newBic = calculateBIC(newInstOfCent, newClusterCenters,
						newMle);
				PFD(D_FOLLOWSPLIT, "newBic " + newBic);
				if (newBic > m_Bic) {
					PFD(D_FOLLOWSPLIT, "*** decide for new clusters");
					m_Bic = newBic;
					m_ClusterCenters = newClusterCenters;
					m_ClusterAssignments = newClusterAssignments;
				} else {
					PFD(D_FOLLOWSPLIT, "*** keep old clusters");
				}
			}

			newNumClusters = m_ClusterCenters.numInstances();
			// decide if finished: max num cluster reached
			// or last centers where not split at all
			if ((newNumClusters >= m_MaxNumClusters)
					|| (newNumClusters == m_NumClusters)) {
				finished = true;
			}
			m_NumClusters = newNumClusters;
		}
	}

	/**
	 * Checks for nominal attributes in the dataset. Class attribute is ignored.
	 * 
	 * @param data
	 *            the data to check
	 * @return false if no nominal attributes are present
	 */
	public boolean checkForNominalAttributes(Instances data) {

		int i = 0;
		
		  while (i < data.numAttributes()) { if ((i != data.classIndex()) &&
		  data.attribute(i++).isNominal()) { return true; } }
		 
		return false;
	}

	/**
	 * Creates and initializes boolean array.
	 * 
	 * @param len
	 *            length of new array
	 * @return the new array
	 */
	boolean[] initBoolArray(int len) {
		boolean[] boolArray = new boolean[len];
		for (int i = 0; i < len; i++) {
			boolArray[i] = false;
		}
		return boolArray;
	}

	/**
	 * Checks the instances. No checks in this KDTree but it calls the check of
	 * the distance function.
	 */
	protected void checkInstances() {

		// m_DistanceF.checkInstances();
	}

	/**
	 * Parses a given list of options.
	 * <p/>
	 * 
	 * <!-- options-start --> Valid options are:
	 * <p/>
	 * 
	 * <pre>
	 * -I &lt;num&gt;
	 *  maximum number of overall iterations
	 *  (default 1).
	 * </pre>
	 * 
	 * <pre>
	 * -M &lt;num&gt;
	 *  maximum number of iterations in the kMeans loop in
	 *  the Improve-Parameter part 
	 *  (default 1000).
	 * </pre>
	 * 
	 * <pre>
	 * -J &lt;num&gt;
	 *  maximum number of iterations in the kMeans loop
	 *  for the splitted centroids in the Improve-Structure part 
	 *  (default 1000).
	 * </pre>
	 * 
	 * <pre>
	 * -L &lt;num&gt;
	 *  minimum number of clusters
	 *  (default 2).
	 * </pre>
	 * 
	 * <pre>
	 * -H &lt;num&gt;
	 *  maximum number of clusters
	 *  (default 4).
	 * </pre>
	 * 
	 * <pre>
	 * -B &lt;value&gt;
	 *  distance value for binary attributes
	 *  (default 1.0).
	 * </pre>
	 * 
	 * <pre>
	 * -use-kdtree
	 *  Uses the KDTree internally
	 *  (default no).
	 * </pre>
	 * 
	 * <pre>
	 * -K &lt;KDTree class specification&gt;
	 *  Full class name of KDTree class to use, followed
	 *  by scheme options.
	 *  eg: &quot;weka.core.neighboursearch.kdtrees.KDTree -P&quot;
	 *  (default no KDTree class used).
	 * </pre>
	 * 
	 * <pre>
	 * -C &lt;value&gt;
	 *  cutoff factor, takes the given percentage of the splitted 
	 *  centroids if none of the children win
	 *  (default 0.0).
	 * </pre>
	 * 
	 * <pre>
	 * -D &lt;distance function class specification&gt;
	 *  Full class name of Distance function class to use, followed
	 *  by scheme options.
	 *  (default weka.core.EuclideanDistance).
	 * </pre>
	 * 
	 * <pre>
	 * -N &lt;file name&gt;
	 *  file to read starting centers from (ARFF format).
	 * </pre>
	 * 
	 * <pre>
	 * -O &lt;file name&gt;
	 *  file to write centers to (ARFF format).
	 * </pre>
	 * 
	 * <pre>
	 * -U &lt;int&gt;
	 *  The debug level.
	 *  (default 0)
	 * </pre>
	 * 
	 * <pre>
	 * -Y &lt;file name&gt;
	 *  The debug vectors file.
	 * </pre>
	 * 
	 * <pre>
	 * -S &lt;num&gt;
	 *  Random number seed.
	 *  (default 10)
	 * </pre>
	 * 
	 * <!-- options-end -->
	 * 
	 * @param options
	 *            the list of options as an array of strings
	 * @throws Exception
	 *             if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {

		String optionString;
		String funcString;

		optionString = Utils.getOption('I', options);
		if (optionString.length() != 0)
			setMaxIterations(Integer.parseInt(optionString));
		else
			setMaxIterations(1);

		optionString = Utils.getOption('M', options);
		if (optionString.length() != 0)
			setMaxKMeans(Integer.parseInt(optionString));
		else
			setMaxKMeans(1000);

		optionString = Utils.getOption('J', options);
		if (optionString.length() != 0)
			setMaxKMeansForChildren(Integer.parseInt(optionString));
		else
			setMaxKMeansForChildren(1000);

		optionString = Utils.getOption('L', options);
		if (optionString.length() != 0)
			setMinNumClusters(Integer.parseInt(optionString));
		else
			setMinNumClusters(2);

		optionString = Utils.getOption('H', options);
		if (optionString.length() != 0)
			setMaxNumClusters(Integer.parseInt(optionString));
		else
			setMaxNumClusters(4);

		optionString = Utils.getOption('B', options);
		if (optionString.length() != 0)
			setBinValue(Double.parseDouble(optionString));
		else
			setBinValue(1.0);

		setUseKDTree(Utils.getFlag("use-kdtree", options));

		if (getUseKDTree()) {
			funcString = Utils.getOption('K', options);
			if (funcString.length() != 0) {
				String[] funcSpec = Utils.splitOptions(funcString);
				if (funcSpec.length == 0) {
					throw new Exception("Invalid function specification string");
				}
				String funcName = funcSpec[0];
				funcSpec[0] = "";
				setKDTree((KDTree) Utils.forName(KDTree.class, funcName,
						funcSpec));
			} else {
				setKDTree(new KDTree());
			}
		} else {
			setKDTree(new KDTree());
		}

		optionString = Utils.getOption('C', options);
		if (optionString.length() != 0)
			setCutOffFactor(Double.parseDouble(optionString));
		else
			setCutOffFactor(0.0);

		funcString = Utils.getOption('D', options);
		if (funcString.length() != 0) {
			String[] funcSpec = Utils.splitOptions(funcString);
			if (funcSpec.length == 0) {
				throw new Exception("Invalid function specification string");
			}
			String funcName = funcSpec[0];
			funcSpec[0] = "";
			setDistanceF((DistanceFunction) Utils.forName(
					DistanceFunction.class, funcName, funcSpec));
		} else {
			setDistanceF(new EuclideanDistance());
		}

		optionString = Utils.getOption('N', options);
		if (optionString.length() != 0) {
			setInputCenterFile(new File(optionString));
			m_CenterInput = new BufferedReader(new FileReader(optionString));
		} else {
			setInputCenterFile(new File(System.getProperty("user.dir")));
			m_CenterInput = null;
		}

		optionString = Utils.getOption('O', options);
		if (optionString.length() != 0) {
			setOutputCenterFile(new File(optionString));
			m_CenterOutput = new PrintWriter(new FileOutputStream(optionString));
		} else {
			setOutputCenterFile(new File(System.getProperty("user.dir")));
			m_CenterOutput = null;
		}

		optionString = Utils.getOption('U', options);
		int debugLevel = 0;
		if (optionString.length() != 0) {
			try {
				debugLevel = Integer.parseInt(optionString);
			} catch (NumberFormatException e) {
				throw new Exception(optionString
						+ "is an illegal value for option -U");
			}
		}
		setDebugLevel(debugLevel);

		optionString = Utils.getOption('Y', options);
		if (optionString.length() != 0) {
			setDebugVectorsFile(new File(optionString));
		} else {
			setDebugVectorsFile(new File(System.getProperty("user.dir")));
			m_DebugVectorsInput = null;
			m_DebugVectors = null;
		}

		super.setOptions(options);
	}

	/**
	 * Gets the current settings of SimpleKMeans.
	 * 
	 * @return an array of strings suitable for passing to setOptions
	 */
	public String[] getOptions() {
		int i;
		Vector<String> result;
		String[] options;

		result = new Vector<String>();

		result.add("-I");
		result.add("" + getMaxIterations());

		result.add("-M");
		result.add("" + getMaxKMeans());

		result.add("-J");
		result.add("" + getMaxKMeansForChildren());

		result.add("-L");
		result.add("" + getMinNumClusters());

		result.add("-H");
		result.add("" + getMaxNumClusters());

		result.add("-B");
		result.add("" + getBinValue());

		if (getUseKDTree()) {
			result.add("-use-kdtree");
			result.add("-K");
			result.add("" + getKDTreeSpec());
		}

		result.add("-C");
		result.add("" + getCutOffFactor());

		if (getDistanceF() != null) {
			result.add("-D");
			result.add("" + getDistanceFSpec());
		}

		if (getInputCenterFile().exists() && getInputCenterFile().isFile()) {
			result.add("-N");
			result.add("" + getInputCenterFile());
		}

		if (getOutputCenterFile().exists() && getOutputCenterFile().isFile()) {
			result.add("-O");
			result.add("" + getOutputCenterFile());
		}

		int dL = getDebugLevel();
		if (dL > 0) {
			result.add("-U");
			result.add("" + getDebugLevel());
		}

		if (getDebugVectorsFile().exists() && getDebugVectorsFile().isFile()) {
			result.add("-Y");
			result.add("" + getDebugVectorsFile());
		}

		options = super.getOptions();
		for (i = 0; i < options.length; i++)
			result.add(options[i]);

		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * Return a string describing this clusterer.
	 * 
	 * @return a description of the clusterer as a string
	 */
	public String toString() {
		StringBuffer temp = new StringBuffer();

		temp.append("\nYouXMeans\n======\n");

		temp.append("Requested iterations            : " + m_MaxIterations
				+ "\n");
		temp.append("Iterations performed            : " + m_IterationCount
				+ "\n");
		if (m_KMeansStopped > 0) {
			temp.append("kMeans did not converge\n");
			temp.append("  but was stopped by max-loops " + m_KMeansStopped
					+ " times (max kMeans-iter)\n");
		}
		temp.append("Splits prepared                 : " + m_NumSplits + "\n");
		temp.append("Splits performed                : " + m_NumSplitsDone
				+ "\n");
		temp.append("Cutoff factor                   : " + m_CutOffFactor
				+ "\n");
		double perc;
		if (m_NumSplitsDone > 0)
			perc = (((double) m_NumSplitsStillDone) / ((double) m_NumSplitsDone)) * 100.0;
		else
			perc = 0.0;
		temp.append("Percentage of splits accepted \n"
				+ "by cutoff factor                : "
				+ Utils.doubleToString(perc, 2) + " %\n");
		temp.append("------\n");

		temp.append("Cutoff factor                   : " + m_CutOffFactor
				+ "\n");
		temp.append("------\n");
		temp.append("\nCluster centers                 : " + m_NumClusters
				+ " centers\n");
		
		for (int i = 0; i < m_NumClusters; i++) {
			temp.append("\nCluster " + i + "\n           ");
			for (int j = 0; j < m_ClusterCenters.numAttributes(); j++) {
				if (m_ClusterCenters.attribute(j).isNominal()) {
					temp.append(" "
							+ m_ClusterCenters.attribute(j)
									.value(
											(int) m_ClusterCenters.instance(i)
													.value(j)));
				} else {
					temp.append(" "
							+ m_ClusterCenters.instance(i).value(j));
				}
			}
		}
		if (m_Mle != null)
			temp.append("\n\nDistortion: "
					+ Utils.doubleToString(Utils.sum(m_Mle), 6) + "\n");
		temp.append("BIC-Value : " + Utils.doubleToString(m_Bic, 6) + "\n");
		temp.append("Number of instances: "+m_Instances.numInstances()+"\n");
		return temp.toString();
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 0.40 $");
	}

	/**
	 * Main method for testing this class.
	 * 
	 * @param argv
	 *            should contain options
	 */
	public static void main(String[] argv) {
		runClusterer(new YouXMeans(), argv);
	}
	
	public Instances getNearestFromCentroids(){
		Instances retInst = new Instances(m_Instances, 0);
		
		for(int i = 0; i <this.m_ClusterCenters.numInstances(); i++){
			double minDist = Double.MAX_VALUE;
			double currDist = Double.MAX_VALUE;
			int minInstance = 0;
	
			for(int j = 0; j< this.m_Instances.numInstances(); j++){
				currDist = this.m_DistanceF.distance(this.m_ClusterCenters.instance(i), this.m_Instances.instance(j));
				
				if(currDist < minDist){
					minInstance = j;
					minDist = currDist;
				}
				
			}
				retInst.add(this.m_Instances.instance(minInstance));				
		}
		return retInst;
	}
}
