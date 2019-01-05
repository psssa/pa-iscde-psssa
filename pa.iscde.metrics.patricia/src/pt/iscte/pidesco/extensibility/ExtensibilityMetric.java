package pt.iscte.pidesco.extensibility;

import java.util.HashMap;

public interface ExtensibilityMetric {
	
	default public void addClassMetric(HashMap<String, String> metricsClass) {}
	
	default public void addProjectMetric(HashMap<String, String> metricsClass) {}
	
	default public void run() {}

}
