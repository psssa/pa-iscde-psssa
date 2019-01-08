package pt.iscte.pidesco.extensibility;

import java.util.List;

/**
 * Represents the metrics that the person wants to add to the project or specific class
 * 
 */
public interface ExtensibilityMetric {

	/**
	 * Metric to add to the project metrics
	 * @return a List that should have only two values.
	 * The first value (position 0) represents the total of that metric in the project
	 * The second value (position 1) represents the the average of that metric in the project
	 */
	public List<Double> addMetricProject();
	
	/**
	 * Metric to add to the class metrics
	 * @return a double with that represents the total of that metric in the class
	 */
	public Double addMetricClass();

}
