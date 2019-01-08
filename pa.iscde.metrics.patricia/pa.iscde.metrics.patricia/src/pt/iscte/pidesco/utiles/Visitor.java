package pt.iscte.pidesco.utiles;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class Visitor extends ASTVisitor {

	private HashMap<String, HashMap<String, HashMap<String, String>>> packageHashMap = new HashMap<>();
	private HashMap<String, HashMap<String, String>> classesNameHashMap;
	private HashMap<String, String> classInfoHashMap;

	private String method = "";
	private String override = "";
	private String parameters = "";
	private String attributeMetrics = "";
	private String attributeStaticMetrics = "";
	
	private String packageName = "";
	
	// visits packages
	@Override
	public boolean visit(PackageDeclaration node) {
		packageName = node.getName().toString();
		
		classesNameHashMap = new HashMap<>();
		
		return true;
	}

	// visits class/interface declaration
	@Override
	public boolean visit(TypeDeclaration node) {
		classInfoHashMap = new HashMap<>();
		if (!node.isInterface()) { // class
			String nameClass = node.getName().toString();
			
			for (MethodDeclaration methodNode : node.getMethods()) { // visit for each method that's in this class
				String nameMethod = methodNode.getName().toString();
				if (!methodNode.isConstructor()) {
					method += nameMethod + ";";
				}
				
				if (methodNode.modifiers().toString().contains((Constants.OVERRIDE))) {
					override += nameMethod +";";
				}
				
				List auxParameters = methodNode.parameters();
				
				for (int i = 0; i < auxParameters.size(); i++) {
					String[] param = ((String) auxParameters.get(i).toString()).split(" ");
							
					parameters += param[1] + ";";
				}
			}
			classInfoHashMap.put(Constants.METHODS, method);
			classInfoHashMap.put(Constants.OVERRIDE, override);
			classInfoHashMap.put(Constants.PARAMETERS, parameters);
			
			for (FieldDeclaration fieldNode : node.getFields()) { // visit attributes that are in this class
				for (Object obj : fieldNode.fragments()) {

					VariableDeclarationFragment var = (VariableDeclarationFragment) obj;
					String nameField = var.getName().toString();
					boolean isStatic = Modifier.isStatic(fieldNode.getModifiers());

					if (isStatic) {
						attributeStaticMetrics += nameField + ";";
						classInfoHashMap.put(Constants.STATIC_ATTRIBUTES, attributeStaticMetrics);
					}else {
						
						attributeMetrics += nameField + ";";
						classInfoHashMap.put(Constants.ATTRIBUTES, attributeMetrics);
					}
				}
			}
			
			classesNameHashMap.put(nameClass, classInfoHashMap);
			if (packageHashMap.containsKey(packageName)) {
				packageHashMap.get(packageName).put(nameClass, classInfoHashMap);
			}else {
				packageHashMap.put(packageName, classesNameHashMap);
			}
			
		} 
		
		//clean metrics for the next class
		method = "";
		override = "";
		parameters ="";
		attributeMetrics = "";
		attributeStaticMetrics = "";

		return true;
	}


	public HashMap<String, HashMap<String, HashMap<String, String>>> getPackageHashMap() {
		return packageHashMap;
	}

	public HashMap<String, HashMap<String, String>> getClassesNameHashMap() {
		return classesNameHashMap;
	}
	
}
