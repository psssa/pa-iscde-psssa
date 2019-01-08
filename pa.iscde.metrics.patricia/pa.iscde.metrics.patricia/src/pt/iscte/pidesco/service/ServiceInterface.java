package pt.iscte.pidesco.service;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * Services offered by the Metrics component.
 */
public interface ServiceInterface {
	
	/**
	 * Get a Table of Metrics for a specific class
	 * @param fullPath (non-null) fullPath of the class
	 * @param className (non-null) only the name of the class without the extension .java that we want to analyze
	 * @param viewArea (non-null) to show the table of metrics
	 * @return a Table of metrics for the desired class 
	 */
	Table getClassMetrics(String fullPath,String className, Composite viewArea);

}
