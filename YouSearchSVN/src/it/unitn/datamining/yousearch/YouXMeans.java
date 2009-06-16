package it.unitn.datamining.yousearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import weka.clusterers.XMeans;
import weka.core.AlgVector;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.core.neighboursearch.KDTree;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

class YouXMeans extends XMeans {
	
	public YouXMeans(){
		super();
		this.getCapabilities().enable(Capability.STRING_ATTRIBUTES);
	}
	
	protected Instances splitCenter(Random random, Instance center,
			double variance, Instances model) throws Exception {
		m_NumSplits++;
		AlgVector r = null;
		Instances children = new Instances(model, 2);
		// what is the policity of splitting in case of tags?
		/*
		 * This function creates: - a random vector (r) - 2 vector based from
		 * center (c and c2)
		 * 
		 * then adds r to c (vector sum) and here it is one new center then
		 * substract r to c2 (vector sum) and here it is onother new center
		 * 
		 * it return 2 centers
		 */

		// here the function halt because there is string value
		/*
		 * r = new AlgVector(model, random); r.changeLength(Math.pow(variance,
		 * 0.5));
		 * 
		 * // creates two support vector based from center AlgVector c = new
		 * AlgVector(center); AlgVector c2 = (AlgVector) c.clone();
		 * 
		 * // add random vector to center c = c.add(r); Instance newCenter =
		 * c.getAsInstance(model, random); children.add(newCenter);
		 * 
		 * // substract random vector to center c2 = c2.substract(r); newCenter
		 * = c2.getAsInstance(model, random); children.add(newCenter);
		 */

		/*
		 * I choose to split center in 2 equal tag parts and assign the
		 * similarity between the tags
		 */
		// create 2 Instance
		Instance i0 = (Instance) center.copy();
		Instance i1 = (Instance) center.copy();
		// create support variables
		String tags = center.stringValue(1);
		ArrayList<String> tagsBin = new ArrayList<String>();
		String tag1 = "";
		String tag2 = "";
		int size = 0;
		Double similarity = center.value(0);

		// extract tags
		StringTokenizer st = new StringTokenizer(tags.trim());
		while (st.hasMoreTokens()) {
			tagsBin.add(st.nextToken());
		}
		size = tagsBin.size();
		// split tags into 2 parts
		if (size > 1) {
			int split = 1;

			String last = "";
			// use one for tag1 and one for tag2
			while (!tagsBin.isEmpty()) {
				split = 1 - split;
				last = tagsBin.get(0);
				if (split == 1) {
					tag2 += " " + last;
				} else {
					tag1 += " " + last;
				}
				tagsBin.remove(0);
			}
			// if odd tag2 has one less tag
			if (size % 2 == 1) {
				tag2 += " " + last;
			}
			i0.setValue(1, tag1);
			i1.setValue(1, tag2);

		} else {
			if (size == 0) {
				// impossible case
				children.add(center);
			} else {
				// size = 1
				tag1 = tagsBin.get(0);
				i0.setValue(1, tag1);
				i1.setValue(1, tag1);

			}
		}

		i0.setValue(0, similarity / 2);
		i1.setValue(0, similarity / 2);

		children.add(i0);
		children.add(i1);
		System.out.println("split(" + tag1 + "," + tag2 + ")");
		return children;
	}

	/**
	 * Generates the X-Means clusterer.
	 * 
	 * @param data
	 *            set of instances serving as training data
	 * @throws Exception
	 *             if the clusterer has not been generated successfully
	 */
	public void buildClusterer(Instances data) throws Exception {

		// can clusterer handle the data?
		// getCapabilities().testWithFail(data);

		m_NumSplits = 0;
		m_NumSplitsDone = 0;
		m_NumSplitsStillDone = 0;

		// replace missing values
		m_ReplaceMissingFilter = new ReplaceMissingValues();
		m_ReplaceMissingFilter.setInputFormat(data);
		m_Instances = Filter.useFilter(data, m_ReplaceMissingFilter);

		// initialize random function
		Random random0 = new Random(m_Seed);

		// num of clusters to start with
		m_NumClusters = m_MinNumClusters;

		// set distance function to default
		if (m_DistanceF == null) {
			m_DistanceF = new YouTagDistance();
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

		// builds up a KDTree
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
	   * Parses a given list of options. <p/>
	   * 
	   <!-- options-start -->
	   * Valid options are: <p/>
	   * 
	   * <pre> -I &lt;num&gt;
	   *  maximum number of overall iterations
	   *  (default 1).</pre>
	   * 
	   * <pre> -M &lt;num&gt;
	   *  maximum number of iterations in the kMeans loop in
	   *  the Improve-Parameter part 
	   *  (default 1000).</pre>
	   * 
	   * <pre> -J &lt;num&gt;
	   *  maximum number of iterations in the kMeans loop
	   *  for the splitted centroids in the Improve-Structure part 
	   *  (default 1000).</pre>
	   * 
	   * <pre> -L &lt;num&gt;
	   *  minimum number of clusters
	   *  (default 2).</pre>
	   * 
	   * <pre> -H &lt;num&gt;
	   *  maximum number of clusters
	   *  (default 4).</pre>
	   * 
	   * <pre> -B &lt;value&gt;
	   *  distance value for binary attributes
	   *  (default 1.0).</pre>
	   * 
	   * <pre> -use-kdtree
	   *  Uses the KDTree internally
	   *  (default no).</pre>
	   * 
	   * <pre> -K &lt;KDTree class specification&gt;
	   *  Full class name of KDTree class to use, followed
	   *  by scheme options.
	   *  eg: "weka.core.neighboursearch.kdtrees.KDTree -P"
	   *  (default no KDTree class used).</pre>
	   * 
	   * <pre> -C &lt;value&gt;
	   *  cutoff factor, takes the given percentage of the splitted 
	   *  centroids if none of the children win
	   *  (default 0.0).</pre>
	   * 
	   * <pre> -D &lt;distance function class specification&gt;
	   *  Full class name of Distance function class to use, followed
	   *  by scheme options.
	   *  (default weka.core.EuclideanDistance).</pre>
	   * 
	   * <pre> -N &lt;file name&gt;
	   *  file to read starting centers from (ARFF format).</pre>
	   * 
	   * <pre> -O &lt;file name&gt;
	   *  file to write centers to (ARFF format).</pre>
	   * 
	   * <pre> -U &lt;int&gt;
	   *  The debug level.
	   *  (default 0)</pre>
	   * 
	   * <pre> -Y &lt;file name&gt;
	   *  The debug vectors file.</pre>
	   * 
	   * <pre> -S &lt;num&gt;
	   *  Random number seed.
	   *  (default 10)</pre>
	   * 
	   <!-- options-end -->
	   * 
	   * @param options the list of options as an array of strings
	   * @throws Exception if an option is not supported
	   */
	  public void setOptions(String[] options)
	    throws Exception {
	    
	    String 	optionString;
	    String 	funcString;

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
		setKDTree((KDTree) Utils.forName(KDTree.class, funcName, funcSpec));
	      }
	      else {
		setKDTree(new KDTree());
	      }
	    }
	    else {
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
	      setDistanceF((DistanceFunction) Utils.forName(DistanceFunction.class,
							    funcName, funcSpec));
	    }
	    else {
	      setDistanceF(new YouTagDistance());
	    }

	    optionString  = Utils.getOption('N', options);
	    if (optionString.length() != 0) {
	      setInputCenterFile(new File(optionString));
	      m_CenterInput = 
		new BufferedReader(new FileReader(optionString));
	    }
	    else {
	      setInputCenterFile(new File(System.getProperty("user.dir")));
	      m_CenterInput = null;
	    }

	    optionString  = Utils.getOption('O', options);
	    if (optionString.length() != 0) {
	      setOutputCenterFile(new File(optionString));
	      m_CenterOutput = new PrintWriter(new FileOutputStream(optionString));
	    }
	    else {
	      setOutputCenterFile(new File(System.getProperty("user.dir")));
	      m_CenterOutput = null;
	    }

	    optionString = Utils.getOption('U', options);
	    int debugLevel = 0;
	    if (optionString.length() != 0) {
	      try {
		debugLevel = Integer.parseInt(optionString);
	      } catch (NumberFormatException e) {
		throw new Exception(optionString +
	                            "is an illegal value for option -U"); 
	      }
	    }
	    setDebugLevel(debugLevel);

	    optionString  = Utils.getOption('Y', options);
	    if (optionString.length() != 0) {
	      setDebugVectorsFile(new File(optionString));
	    }
	    else {
	      setDebugVectorsFile(new File(System.getProperty("user.dir")));
	      m_DebugVectorsInput = null;
	      m_DebugVectors      = null;
	    }
	    
	    super.setOptions(options);
	  }
	  
}