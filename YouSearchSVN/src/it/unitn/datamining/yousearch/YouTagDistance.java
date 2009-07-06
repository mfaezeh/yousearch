/*    This program is free software; you can redistribute it and/or modify
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
 *    YouTagDistance.java
 *    Copyright (C) 2009 Science University, Trento, Italy
 *
 */

package it.unitn.datamining.yousearch;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.NormalizableDistance;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.neighboursearch.PerformanceStats;

/**
 * <!-- globalinfo-start --> Implementing YouTag distance (or similarity)
 * function.<br/>
 * <br/>
 * One object defines not one distance but the data model in which the distances
 * between objects of that data model can be computed.<br/>
 * <br/>
 * Attention: For efficiency reasons the use of consistency checks (like are the
 * data models of the two instances exactly the same), is low.<br/>
 * <br/>
 * For more information, see:<br/>
 * <br/>
 * Wikipedia. YouTag distance. URL http://en.wikipedia.org/wiki/YouTag_distance.
 * <p/>
 * <!-- globalinfo-end -->
 * 
 * <!-- technical-bibtex-start --> BibTeX:
 * 
 * <pre>
 * &#064;misc{missing_id,
 *    author = {Wikipedia},
 *    title = {YouTag distance},
 *    URL = {http://en.wikipedia.org/wiki/YouTag_distance}
 * }
 * </pre>
 * <p/>
 * <!-- technical-bibtex-end -->
 * 
 * <!-- options-start --> Valid options are:
 * <p/>
 * 
 * <pre>
 * -D
 *  Turns off the normalization of attribute 
 *  values in distance calculation.
 * </pre>
 * 
 * <pre>
 * -R &lt;col1,col2-col4,...&gt;
 *  Specifies list of columns to used in the calculation of the 
 *  distance. 'first' and 'last' are valid indices.
 *  (default: first-last)
 * </pre>
 * 
 * <pre>
 * -V
 *  Invert matching sense of column indices.
 * </pre>
 * 
 * <!-- options-end -->
 * 
 * @author Gabi Schmidberger (gabi@cs.waikato.ac.nz)
 * @author Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.13 $
 */
public class YouTagDistance extends NormalizableDistance implements Cloneable,
		TechnicalInformationHandler {
	/** for serialization. */
	private static final long serialVersionUID = 1068606253458807903L;

	/**
	 * Constructs an YouTag Distance object, Instances must be still set.
	 */
	public YouTagDistance() {
		super();
	}

	/**
	 * Constructs an YouTag Distance object and automatically initializes the
	 * ranges.
	 * 
	 * @param data
	 *            the instances the distance function should work on
	 */
	public YouTagDistance(Instances data) {
		super(data);
	}

	/**
	 * Returns a string describing this object.
	 * 
	 * @return a description of the evaluator suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Implementing YouTag distance (or similarity) function.\n\n"
				+ "One object defines not one distance but the data model in which "
				+ "the distances between objects of that data model can be computed.\n\n"
				+ "Attention: For efficiency reasons the use of consistency checks "
				+ "(like are the data models of the two instances exactly the same), "
				+ "is low.\n\n" + "For more information, see:\n\n"
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

		result = new TechnicalInformation(Type.MISC);
		result.setValue(Field.AUTHOR, "Wikipedia");
		result.setValue(Field.TITLE, "YouTag distance");
		result.setValue(Field.URL,
				"http://en.wikipedia.org/wiki/YouTag_distance");

		return result;
	}

	private double getCountCommonTag(String tag1, String tag2) {
		String[] tagArr1 = tag1.split(" ");
		String[] tagArr2 = tag2.split(" ");
		int count = 0;
		for (String tag : tagArr1) {
			for (String tagComp : tagArr2) {
				if (tag.equalsIgnoreCase(tagComp))
					count++;
			}
		}
		return Double.valueOf(count);
	}

	/**
	 * Calculates the distance between two instances.
	 * 
	 * @param first
	 *            the first instance
	 * @param second
	 *            the second instance
	 * @return the distance between the two given instances
	 */
	public double distance(Instance first, Instance second) {
		double favg = first.value(0);
		double savg = second.value(0);
		double fsum = first.value(1);
		double ssum = second.value(1);

		double common_tag = getCountCommonTag(first.stringValue(2), second
				.stringValue(2));
		double tag1n = fsum / favg;
		double tag2n = ssum / savg;

		if ((tag1n < 2.1) && (tag2n < 2.1)) {
			first.setValue(0, 0.1);
			first.setValue(1, 5.0);
			second.setValue(0, 0.1);
			second.setValue(1, 5.0);
			return 0.0;
		} else if (tag1n < 2.1) {
			first.setValue(0, 0.1);
			first.setValue(1, 5.0);
		} else if (tag2n < 2.1) {
			second.setValue(0, 0.1);
			second.setValue(1, 5.0);
		}

		if ((favg == 0.1) && (savg == 0.1))
			return 0.0;
		else {
			if (favg == 0.1)
				if ((tag2n) < 2.1) {
					second.setValue(0, 0.1);
					second.setValue(1, 5.0);
					return 0.0;
				} else
					return 1000;
			else if (savg == 0.1)
				if ((tag1n) < 2.1) {
					first.setValue(0, 0.1);
					first.setValue(1, 5.0);
					return 0.0;
				} else
					return 1000;
		}
		common_tag = (common_tag < 1) ? 0.001 : common_tag;
		return Math.abs(((Math.pow(fsum, 2) - Math.pow(ssum, 2)))
				* ((Math.pow(favg, 2) - Math.pow(savg, 2))))
				/ common_tag;

	}

	/**
	 * Calculates the distance (or similarity) between two instances. Need to
	 * pass this returned distance later on to postprocess method to set it on
	 * correct scale. <br/>
	 * P.S.: Please don't mix the use of this function with distance(Instance
	 * first, Instance second), as that already does post processing. Please
	 * consider passing Double.POSITIVE_INFINITY as the cutOffValue to this
	 * function and then later on do the post processing on all the distances.
	 * 
	 * @param first
	 *            the first instance
	 * @param second
	 *            the second instance
	 * @param stats
	 *            the structure for storing performance statistics.
	 * @return the distance between the two given instances or
	 *         Double.POSITIVE_INFINITY.
	 */
	public double distance(Instance first, Instance second,
			PerformanceStats stats) { // debug method pls remove after use
		return Math.sqrt(distance(first, second, Double.POSITIVE_INFINITY,
				stats));
	}

	/**
	 * Updates the current distance calculated so far with the new difference
	 * between two attributes. The difference between the attributes was
	 * calculated with the difference(int,double,double) method.
	 * 
	 * @param currDist
	 *            the current distance calculated so far
	 * @param diff
	 *            the difference between two new attributes
	 * @return the update distance
	 * @see #difference(int, double, double)
	 */
	protected double updateDistance(double currDist, double diff) {
		double result;

		result = currDist;
		result += diff * diff;

		return result;
	}

	/**
	 * Does post processing of the distances (if necessary) returned by
	 * distance(distance(Instance first, Instance second, double cutOffValue).
	 * It is necessary to do so to get the correct distances if
	 * distance(distance(Instance first, Instance second, double cutOffValue) is
	 * used. This is because that function actually returns the squared distance
	 * to avoid inaccuracies arising from floating point comparison.
	 * 
	 * @param distances
	 *            the distances to post-process
	 */
	public void postProcessDistances(double distances[]) {
		for (int i = 0; i < distances.length; i++) {
			distances[i] = Math.sqrt(distances[i]);
		}
	}

	/**
	 * Returns the squared difference of two values of an attribute.
	 * 
	 * @param index
	 *            the attribute index
	 * @param val1
	 *            the first value
	 * @param val2
	 *            the second value
	 * @return the squared difference
	 */
	public double sqDifference(int index, double val1, double val2) {
		double val = difference(index, val1, val2);
		return val * val;
	}

	/**
	 * Returns value in the middle of the two parameter values.
	 * 
	 * @param ranges
	 *            the ranges to this dimension
	 * @return the middle value
	 */
	public double getMiddle(double[] ranges) {

		double middle = ranges[R_MIN] + ranges[R_WIDTH] * 0.5;
		return middle;
	}

	/**
	 * Returns the index of the closest point to the current instance. Index is
	 * index in Instances object that is the second parameter.
	 * 
	 * @param instance
	 *            the instance to assign a cluster to
	 * @param allPoints
	 *            all points
	 * @param pointList
	 *            the list of points
	 * @return the index of the closest point
	 * @throws Exception
	 *             if something goes wrong
	 */
	public int closestPoint(Instance instance, Instances allPoints,
			int[] pointList) throws Exception {
		double minDist = Integer.MAX_VALUE;
		int bestPoint = 0;
		for (int i = 0; i < pointList.length; i++) {
			double dist = distance(instance, allPoints.instance(pointList[i]),
					Double.POSITIVE_INFINITY);
			if (dist < minDist) {
				minDist = dist;
				bestPoint = i;
			}
		}
		return pointList[bestPoint];
	}

	/**
	 * Returns true if the value of the given dimension is smaller or equal the
	 * value to be compared with.
	 * 
	 * @param instance
	 *            the instance where the value should be taken of
	 * @param dim
	 *            the dimension of the value
	 * @param value
	 *            the value to compare with
	 * @return true if value of instance is smaller or equal value
	 */
	public boolean valueIsSmallerEqual(Instance instance, int dim, double value) { // This
																					// stays
		return instance.value(dim) <= value;
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 1.0 $");
	}
}