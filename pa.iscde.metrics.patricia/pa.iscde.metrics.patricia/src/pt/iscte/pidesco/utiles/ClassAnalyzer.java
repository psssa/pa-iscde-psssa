package pt.iscte.pidesco.utiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

public class ClassAnalyzer extends CalculateMetrics{

	public int readClass(String classPath) {
		int numberLines = 0;
			File file = new File(classPath);
			try {
				FileReader fileReader =  new FileReader(file);
				BufferedReader bufferedReader =  new BufferedReader(fileReader);
				
				while(( bufferedReader.readLine()) != null) {
					numberLines++;
				}   
				bufferedReader.close();         
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		return numberLines;
	}
	
	
	public void transferMetrics(String workspace, Visitor visitor, HashMap<String, Integer> linesOfClasses) {
		String[] project = workspace.split("/");
		int lineCodeProject = 0;
		
		for (String keyPackage : visitor.getPackageHashMap().keySet()) {
			HashMap<String, HashMap<String, String>> classes = visitor.getPackageHashMap().get(keyPackage);
			for (String keyClass : classes.keySet()) {
				int linesCodeClass = linesOfClasses.get(keyClass);
				lineCodeProject += linesCodeClass;
				
				File classFile = new File(workspace + "/" + keyClass + ".txt");
				calculateTotalOfMetrics(false, visitor, keyClass);
				writeInFile(classFile, true, linesCodeClass);
			}
		}
		
		File projectFile = new File(workspace + "/" + project[project.length-1] + ".txt");
		calculateTotalOfMetrics(true, visitor, null);
		writeInFile(projectFile, true, lineCodeProject);
	}


	private void writeInFile(File nameFile, boolean isProject, int linesOfClasses) {
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nameFile), "utf-8"));
		    writer.write("         " + nameFile.getName().replace(".txt", "").toUpperCase() +"\n\n"); 
		    
		    writer.write("Metric                       Total\n");
		    if (isProject) {
		    	writer.write("Number of Packages             " + getNumberOfPackages() + "\n");
		    	writer.write("Number of Classes              " + getNumberOfClasses() + "\n");
			}
		    writer.write("Number of Methods              " + getNumberOfMethods() + "\n");
		    writer.write("Number of Methods Override     " + getNumberOfOverride() + "\n");
		    writer.write("Number of Static Attributes    " + getNumberOfStaticAttributes() + "\n");
		    writer.write("Number of Attributes           " + getNumberOfAttributes() + "\n");

		    writer.write("Total Lines of Code            " + linesOfClasses + "\n");
		    
		    if (!isProject) {
		    	writer.write("Number of Parameters           " + getNumberOfParameters() + "\n");
			}
		    
		} catch (IOException ex) {
		 
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
		
	}
}
