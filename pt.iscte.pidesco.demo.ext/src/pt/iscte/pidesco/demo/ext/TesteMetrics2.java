package pt.iscte.pidesco.demo.ext;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.pidesco.extensibility.ExtensibilityMetric;

public class TesteMetrics2 implements ExtensibilityMetric {

	public TesteMetrics2() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Double> addMetricProject() {
		List<Double> testValues = new ArrayList<Double>();
		
		testValues.add(new Double(3));
		testValues.add(new Double(2.0));
		
		return testValues;
	}

	@Override
	public Double addMetricClass() {
		return new Double(1.12);
	}

}
