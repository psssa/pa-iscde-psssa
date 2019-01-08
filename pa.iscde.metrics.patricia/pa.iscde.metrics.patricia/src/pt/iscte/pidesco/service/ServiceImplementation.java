package pt.iscte.pidesco.service;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import pt.iscte.pidesco.utiles.CalculateMetrics;
import pt.iscte.pidesco.utiles.ClassAnalyzer;
import pt.iscte.pidesco.utiles.Constants;
import pt.iscte.pidesco.utiles.JavaParser;
import pt.iscte.pidesco.utiles.Visitor;

public class ServiceImplementation extends CalculateMetrics implements ServiceInterface{
	
	private Visitor visitor;
	private ClassAnalyzer classAnalyzer;
	
	public ServiceImplementation() {
		visitor = new Visitor();
		classAnalyzer = new ClassAnalyzer();
	}

	/**
	 * 
	 * @param className
	 */
	@Override
	public Table getClassMetrics(String fullPath, String className, Composite viewArea) {
		Table tableWithMetrics  = new Table(viewArea,SWT.MULTI | SWT.FULL_SELECTION);
		int numberTitles = Constants.TITLES.length;
		int numberRows = Constants.NAME_ROWS.length;	
		
		JavaParser.parse(fullPath, visitor);		
		
		calculateTotalOfMetrics(false, visitor, className);
		int numberOfLines = classAnalyzer.readClass(fullPath);
		
		for (int i = 0; i < numberTitles - 1; i++) {
			TableColumn column = new TableColumn(tableWithMetrics, SWT.NONE);
			column.setText(Constants.TITLES[i]);
		}
		
		for (int j = 0; j < numberRows - 1 ; j++) {
			TableItem item = new TableItem(tableWithMetrics, SWT.NONE);
			putData(item, j, numberOfLines);
		}
		
		for (int k = 0; k < numberTitles -1 ; k++) {
			tableWithMetrics.getColumn(k).pack();
		}
		tableWithMetrics.setSize(tableWithMetrics.computeSize(SWT.DEFAULT, 200));
		viewArea.layout();
		
		return tableWithMetrics;
		
	}
	
	private void analise(File workspace, String className) {
		ArrayList<File> directories = new ArrayList<>();

		if (!workspace.isFile()) {
			File[] files = workspace.listFiles();
			for (File file : files) {
				if (!file.isFile()) { // package || folder
					directories.add(file);
				} else {
					if (file.getName().endsWith(".java")) { // class
						if (file.getName().equals(className)) {
							
						}
					}
				}
			}
		}
		for (File file : directories) { // file (repetir o método
			analise(file, className);
		}
	}
	
	//tentar meter este metodo numa classe à parte visto que é usado tambem no metricseditor
	private void putData(TableItem item, int posicion, int totalLines) {
		item.setText(0, Constants.NAME_ROWS[posicion]);
		if (Constants.NAME_ROWS[posicion].endsWith(Constants.PACKAGES)) {
			item.setText(1, String.valueOf(getNumberOfPackages()));
			System.out.println("getNumberOfPackages() : " + getNumberOfPackages());
		} else if (Constants.NAME_ROWS[posicion].contains(Constants.CLASSES)) {
			item.setText(1, String.valueOf(getNumberOfClasses()));
			System.out.println("getNumberOfClasses() : " + getNumberOfClasses());
		} else if (Constants.NAME_ROWS[posicion].endsWith(Constants.METHODS)) {
			item.setText(1, String.valueOf(getNumberOfMethods()));
			System.out.println("getNumberOfMethods() : " + getNumberOfMethods());
		} else if (Constants.NAME_ROWS[posicion].endsWith(Constants.OVERRIDE)) {
			item.setText(1, String.valueOf(getNumberOfOverride()));
			System.out.println("getNumberOfOverride() : " + getNumberOfOverride());
		} else if (Constants.NAME_ROWS[posicion].endsWith(Constants.PARAMETERS)) {
			item.setText(1, String.valueOf(getNumberOfParameters()));
			System.out.println("getNumberOfParameters() : " + getNumberOfParameters());
		} else if (Constants.NAME_ROWS[posicion].endsWith(Constants.STATIC_ATTRIBUTES)) {
			item.setText(1, String.valueOf(getNumberOfStaticAttributes()));
			System.out.println("getNumberOfStaticAttributes() : " + getNumberOfStaticAttributes());
		} else if (Constants.NAME_ROWS[posicion].endsWith(Constants.ATTRIBUTES)) {
			item.setText(1, String.valueOf(getNumberOfAttributes()));
			System.out.println("getNumberOfAttributes() : " + getNumberOfAttributes());
		} else if (Constants.NAME_ROWS[posicion].endsWith(Constants.TOTAL_LINES)) {
			item.setText(1, String.valueOf(totalLines));
			System.out.println("totalLines : " + totalLines);
		}

	}

}
