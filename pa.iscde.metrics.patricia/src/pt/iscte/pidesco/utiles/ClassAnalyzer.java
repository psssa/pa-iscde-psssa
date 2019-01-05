package pt.iscte.pidesco.utiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

public class ClassAnalyzer extends CalculateMetrics{ // ler a nossa classe para contar o numero total de linhas

	public int readClass(String classPath) {
		int numberLines = 0;
			File file = new File(classPath);
			try {
				// FileReader reads text files in the default encoding.
				FileReader fileReader =  new FileReader(file);
				
				// Always wrap FileReader in BufferedReader.
				BufferedReader bufferedReader =  new BufferedReader(fileReader);
				
				while(( bufferedReader.readLine()) != null) {
					numberLines++;
				}   
				// Always close files.
				bufferedReader.close();         
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return numberLines;
	}
	
	
	public void transferMetrics(String workspace, Visitor visitor, HashMap<String, Integer> linesOfClasses) {
		/* FILE PROJECT : chamar o calculateTotalOfMetrics e criar um FileOutputStream com o nome do projeto */
		String[] project = workspace.split("/");
		int lineCodeProject = 0;
		
		/* FILE CLASS:	por cada keyClass chamar o calculateTotalOfMetrics e criar um FileOutputStream com o nome da classe (KeyClass) */
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
		    writer.write("             " + nameFile.getName().toUpperCase().replace(".txt", "") +"\n\n"); //REMOVER A PARTE DO TXT E METER O TITULO MAIS CENTRADO --> FALTA FAZER PONTO EXTENSAO
		    
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
