package pt.iscte.pidesco.metrics;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import pt.iscte.pidesco.extensibility.ExtensibilityMetric;
import pt.iscte.pidesco.extensibility.PidescoView;
import pt.iscte.pidesco.javaeditor.service.JavaEditorListener;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;
import pt.iscte.pidesco.utiles.CalculateMetrics;
import pt.iscte.pidesco.utiles.ClassAnalyzer;
import pt.iscte.pidesco.utiles.Constants;
import pt.iscte.pidesco.utiles.JavaParser;
import pt.iscte.pidesco.utiles.Visitor;

public class MetricsEditor extends CalculateMetrics implements PidescoView {

	private Visitor visitor;
	private Table tableProject;
	private ClassAnalyzer classAnalyzer;

	//Info Class
	private ArrayList<String> classList;
	private HashMap<String, Integer> linesOfClasses;
	private String className = "";

	//Count the metrics
	private int numberOfClasses = 0;
	private int numberOfPackages = 0;
	private int numberOfMethods = 0;	
	private int numberOfOverride = 0;	
	private int numberOfAttributes= 0;
	private int numberOfStaticAttributes = 0;
	private int numberOfParameters = 0;
	private int numberOfTotalLines = 0;
	
	
	//Lists
	private String[] titles = { "Metric", "Total", "Average" };
	private int numberTitles = titles.length;
	private String[] nameRows = new String[] { "Number of Packages", "Number of Classes",
			"Number of Methods", "Number of Methods Override", "Number of Static Attributes",
			"Number of Attributes", "Total Lines of Code" , "Number of Parameters"};
	private int numberRows = nameRows.length;
	
	
	public MetricsEditor() {
		visitor = new Visitor();
		classAnalyzer = new ClassAnalyzer();
		classList = new ArrayList<>();
		linesOfClasses = new HashMap<>();
		
	}
	
	@Override
	public void createContents(Composite viewArea, Map<String, Image> imageMap) {
		BundleContext context = Activator.getContext();

		RowLayout layout = new RowLayout(SWT.VERTICAL);
		viewArea.setLayout(layout);
		layout.justify = true;

		ServiceReference<JavaEditorServices> serviceReference = context.getServiceReference(JavaEditorServices.class);
		JavaEditorServices editor = context.getService(serviceReference);
		

		editor.addListener(new JavaEditorListener() {

			@Override
			public void selectionChanged(File file, String text, int offset, int length) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fileSaved(File file) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fileOpened(File file) {
				className = file.getName().toString();
			}

			@Override
			public void fileClosed(File file) {
				// TODO Auto-generated method stub

			}
		});
		
		
		// Buscar o workspace
		String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		System.out.println("WORKSPACE : " + workspace);
		
		File basedir = new File(workspace);
		getFiles(basedir);
		
		for (int i = 0; i < classList.size(); i++) { // visitar as classes
			JavaParser.parse(classList.get(i), visitor);
			int numberOfLines = classAnalyzer.readClass(classList.get(i));
			numberOfTotalLines += numberOfLines;
			
			String[] name = classList.get(i).split("\\\\");
			linesOfClasses.put(name[name.length-1].replace(".java", ""), numberOfLines);
			
		}
		
		getNewContent();
		
		tableProject = new Table(viewArea, SWT.MULTI | SWT.FULL_SELECTION);
		tableProject.setVisible(false);
		
		// PROJECT
		showProjectMetrics(viewArea);

		// CLASS
		showClassMetrics(viewArea);
		
		//METRICS
		createMetricsFile(viewArea, workspace);
		
		
	}
	
	//extension point
	private void getNewContent() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = reg.getConfigurationElementsFor("pa.iscde.metrics.patricia.metricsExtension");
		System.out.println(elements.length);
		for(int i = 0 ; i < elements.length ; i++) {
			try {
				System.out.println(elements[i]);
				ExtensibilityMetric action = (ExtensibilityMetric) elements[i].createExecutableExtension("class");
				//substituir o null depois pelos HashMaps
				
				action.run();
				System.out.println("this");
//				action.addClassMetric(null);
//				action.addProjectMetric(null);

//				for(int j = 0; j<extraClassMetrics.size();j++) {
//
//					if(!metricListClass.contains(extraClassMetrics.get(j))) {
//						metricListClass.add(extraClassMetrics.get(j));
//						linesClassA.add(extraClassMetrics.get(j).getName());
//					}
//				}
//
//				for(int j = 0; j<extraPackageMetrics.size(); j++) {
//					metricListPackage.add(extraPackageMetrics.get(j));
//				}

			} catch (CoreException e1) {
				e1.printStackTrace();
			}

		}

	}

	private void createMetricsFile(Composite viewArea, String workspace) {
		Button metricsProject = new Button(viewArea, SWT.PUSH);
		metricsProject.setText("Generate Metrics File");
		metricsProject.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				classAnalyzer.transferMetrics(workspace, visitor, linesOfClasses);
				MessageDialog.openInformation(new Shell(), "Metrics Info", "Go check your files with the metrics");
			}
		});
	}

	// Saber as classes existente no workspace
	private void getFiles(File workspace) {
		ArrayList<File> directories = new ArrayList<>();

		if (!workspace.isFile()) {
			File[] files = workspace.listFiles();
			for (File file : files) {
				if (!file.isFile()) { // package || folder
					directories.add(file);
				} else {
					if (file.getName().endsWith(".java")) { // class
						classList.add(file.getAbsolutePath());
					}
				}
			}
		}
		for (File file : directories) { // file (repetir o método
			getFiles(file);
		}
	}
	

	// Will show us the metrics of the project
	private void showProjectMetrics(Composite viewArea) {

		Button metricsProject = new Button(viewArea, SWT.PUSH);
		metricsProject.setText("Metrics of the Project");
		metricsProject.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				
				calculateTotalOfMetrics(true, visitor, "");
				loadData();
				switch (event.type) {
				case SWT.Selection:
					if (tableProject.isEnabled()) {
						tableProject.dispose();
						tableProject = new Table(viewArea, SWT.MULTI | SWT.FULL_SELECTION);
					}

					tableProject.setLinesVisible(true);
					tableProject.setHeaderVisible(true);
					
					for (int i = 0; i < numberTitles ; i++) {
						TableColumn column = new TableColumn(tableProject, SWT.NONE);
						column.setText(titles[i]);
					}
					
					for (int j = 0; j < numberRows - 1 ; j++) {
						TableItem item = new TableItem(tableProject, SWT.NONE);
						putData(item, j, numberOfTotalLines);
					}
					
					for (int k = 0; k < numberTitles ; k++) {
						tableProject.getColumn(k).pack();
					}
					tableProject.setSize(tableProject.computeSize(SWT.DEFAULT, 200));
					viewArea.layout();
					
					break;
				}
				clearData();
			}
		});
	}
	
	
	// Will show us the metrics of the class
	private void showClassMetrics(Composite viewArea) { 
		Button metricsClass = new Button(viewArea, SWT.PUSH);
		metricsClass.setText("Metrics of the Class");
	
		metricsClass.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!"".equals(className)) {

					className = className.replace(".java", "");
					int auxTotalLines = 0;
					for (String auxClass : linesOfClasses.keySet()) {
						if (className.equals(auxClass)) {
							auxTotalLines = linesOfClasses.get(auxClass);
						}
					}
					calculateTotalOfMetrics(false, visitor,className);
					loadData();
					switch (event.type) {
					case SWT.Selection:
						if (tableProject.isEnabled()) {
							tableProject.dispose();
							tableProject = new Table(viewArea, SWT.MULTI | SWT.FULL_SELECTION);
						}
						tableProject.setLinesVisible(true);
						tableProject.setHeaderVisible(true);

						for (int i = 0; i < numberTitles - 1; i++) {
							TableColumn column = new TableColumn(tableProject, SWT.NONE);
							column.setText(titles[i]);
						}

						for (int j = 2; j < numberRows; j++) {
							TableItem item = new TableItem(tableProject, SWT.NONE);
							putData(item, j, auxTotalLines);
						}

						for (int k = 0; k < numberTitles - 1; k++) {
							tableProject.getColumn(k).pack();
						}

						tableProject.setSize(tableProject.computeSize(SWT.DEFAULT, 200));
						viewArea.layout();

						break;
					}
					clearData();
				}else {
					MessageDialog.openError(new Shell(), "Error", "Please select the class that you want to analize");
				}
			}
		});
	}
	
	private void putData(TableItem item, int posicion, int totalLines) {
		item.setText(0, nameRows[posicion]);
		double average = 0;
		if (nameRows[posicion].endsWith(Constants.PACKAGES)) {
			item.setText(1, String.valueOf(numberOfPackages));
		}else if (nameRows[posicion].contains(Constants.CLASSES)) {
			item.setText(1, String.valueOf(numberOfClasses));
			//CLASSE POR PACKAGE 
			average =(float) numberOfClasses / numberOfPackages;
		}
		else if (nameRows[posicion].endsWith(Constants.METHODS)) {
			item.setText(1, String.valueOf(numberOfMethods));
			//MÉTODOS POR CLASSE
			average = (float) numberOfMethods / numberOfClasses;
		}
		else if (nameRows[posicion].endsWith(Constants.OVERRIDE)) {
			item.setText(1, String.valueOf(numberOfOverride));
			//MÉTODOS OVERRIDE POR METODOS TOTAIS
			average = (float) numberOfOverride / numberOfMethods;
		}
		else if (nameRows[posicion].endsWith(Constants.PARAMETERS)) {
			item.setText(1, String.valueOf(numberOfParameters));
			//PARAMETERS POR METODO
			average = (float) numberOfParameters / numberOfMethods;
		}
		else if (nameRows[posicion].endsWith(Constants.STATIC_ATTRIBUTES)) {
			item.setText(1, String.valueOf(numberOfStaticAttributes));
			//STATIC ATTRIBUTES POR NUMERO ATTRIBUTES : 
			average= (float) numberOfStaticAttributes / numberOfAttributes;
		}
		else if (nameRows[posicion].endsWith(Constants.ATTRIBUTES)) {
			item.setText(1, String.valueOf(numberOfAttributes));
			//ATTRIBUTES POR CLASSE : 
			average= (float) numberOfAttributes / numberOfClasses;
		}
		else if (nameRows[posicion].endsWith(Constants.TOTAL_LINES)) {
			item.setText(1, String.valueOf(totalLines));
		}
		
		if (average <= 0) {
			item.setText(2, "-");
		}else {
			DecimalFormat df = new DecimalFormat("#.###");
			item.setText(2, String.valueOf(df.format(average)));
			
		}
	}
	
	private void loadData() {
		numberOfPackages =  getNumberOfPackages();
		numberOfClasses =  getNumberOfClasses();
		numberOfMethods = getNumberOfMethods();
		numberOfOverride = getNumberOfOverride();
		numberOfParameters = getNumberOfParameters();
		numberOfAttributes = getNumberOfAttributes();
		numberOfStaticAttributes = getNumberOfStaticAttributes();
	}
	
	private void clearData() {
		setNumberOfPackages(0);
		setNumberOfClasses(0);
		setNumberOfMethods(0);
		setNumberOfOverride(0);
		setNumberOfParameters(0);
		setNumberOfAttributes(0);
		setNumberOfStaticAttributes(0);
	}
}
