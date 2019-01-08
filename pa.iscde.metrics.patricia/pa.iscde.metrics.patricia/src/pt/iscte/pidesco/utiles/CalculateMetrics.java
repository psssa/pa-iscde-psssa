package pt.iscte.pidesco.utiles;

import java.util.HashMap;

public class CalculateMetrics {
	
	private int numberOfClasses = 0;
	private int numberOfPackages = 0;
	private int numberOfMethods = 0;	
	private int numberOfOverride = 0;	
	private int numberOfAttributes= 0;
	private int numberOfStaticAttributes = 0;
	private int numberOfParameters = 0;
	
	protected void calculateTotalOfMetrics(boolean isProject, Visitor visitor, String className){
		numberOfPackages = visitor.getPackageHashMap().size();
		for (String keyPackage : visitor.getPackageHashMap().keySet()) {
			HashMap<String, HashMap<String, String>> classes = visitor.getPackageHashMap().get(keyPackage);
			for (String keyClass : classes.keySet()) { 
				HashMap<String, String> attributes = null;

				if (!isProject && keyClass.equals(className)) {
					attributes = classes.get(className);
					calculateClassMetrics(attributes);
					break;
				} else if (isProject) {					
					attributes = classes.get(keyClass);
					numberOfClasses += classes.size();
					calculateClassMetrics(attributes);
				}
			}
		}
	}
	
	private void calculateClassMetrics(HashMap<String, String> attributes) {
		for (String keyAttribute : attributes.keySet()) {
			String[] arrayAttributes = null;
			arrayAttributes = attributes.get(keyAttribute).split(";");
			int number = 0;
			if (arrayAttributes[0] != "" && arrayAttributes[0] != null ) {
				number =  arrayAttributes.length;
			}
			
			if (keyAttribute.equals(Constants.METHODS)) {
				numberOfMethods += number;
			} else if (keyAttribute.equals(Constants.OVERRIDE)) {
				numberOfOverride += number;
			} else if (keyAttribute.equals(Constants.ATTRIBUTES)) {
				numberOfAttributes += number;
			} else if (keyAttribute.equals(Constants.STATIC_ATTRIBUTES)) {
				numberOfStaticAttributes += number;
			} else if (keyAttribute.equals(Constants.PARAMETERS)) {
				numberOfParameters += number;
			}

		}
	}
	
	protected int getNumberOfClasses() {
		return numberOfClasses;
	}

	protected void setNumberOfClasses(int numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}

	protected int getNumberOfPackages() {
		return numberOfPackages;
	}

	protected void setNumberOfPackages(int numberOfPackages) {
		this.numberOfPackages = numberOfPackages;
	}

	protected int getNumberOfMethods() {
		return numberOfMethods;
	}

	protected void setNumberOfMethods(int numberOfMethods) {
		this.numberOfMethods = numberOfMethods;
	}

	protected int getNumberOfOverride() {
		return numberOfOverride;
	}

	protected void setNumberOfOverride(int numberOfOverride) {
		this.numberOfOverride = numberOfOverride;
	}

	protected int getNumberOfAttributes() {
		return numberOfAttributes;
	}

	protected void setNumberOfAttributes(int numberOfAttributes) {
		this.numberOfAttributes = numberOfAttributes;
	}

	protected int getNumberOfStaticAttributes() {
		return numberOfStaticAttributes;
	}

	protected void setNumberOfStaticAttributes(int numberOfStaticAttributes) {
		this.numberOfStaticAttributes = numberOfStaticAttributes;
	}

	protected int getNumberOfParameters() {
		return numberOfParameters;
	}

	protected void setNumberOfParameters(int numberOfParameters) {
		this.numberOfParameters = numberOfParameters;
	}
}
