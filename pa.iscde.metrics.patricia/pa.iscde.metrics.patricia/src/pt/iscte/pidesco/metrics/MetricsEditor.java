package pt.iscte.pidesco.metrics;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import pt.iscte.pidesco.service.ServiceImplementation;
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
	public int numberTitles = Constants.TITLES.length;
	public int numberRows = Constants.NAME_ROWS.length;	
	
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
			}

			@Override
			public void fileSaved(File file) {
			}

			@Override
			public void fileOpened(File file) {
				className = file.getName().toString();
			}

			@Override
			public void fileClosed(File file) {
			}
		});
		
		
		// Buscar o workspace
		String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		File basedir = new File(workspace);
		getFiles(basedir);
		
		for (int i = 0; i < classList.size(); i++) { // visitar as classes
			JavaParser.parse(classList.get(i), visitor);
			int numberOfLines = classAnalyzer.readClass(classList.get(i));
			numberOfTotalLines += numberOfLines;
			
			String[] name = classList.get(i).split("\\\\");
			linesOfClasses.put(name[name.length-1].replace(".java", ""), numberOfLines);
			
		}
	
		tableProject = new Table(viewArea, SWT.MULTI | SWT.FULL_SELECTION);
		tableProject.setVisible(false);
		
		// PROJECT
		showProjectMetrics(viewArea);

		// CLASS
		showClassMetrics(viewArea);
		
//		testService(viewArea);
		
		//METRICS
		createMetricsFile(viewArea, workspace);
		
		
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
						column.setText(Constants.TITLES[i]);
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
					
					//extension point
					getNewContent(true);
					
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
							column.setText(Constants.TITLES[i]);
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

						//extension point
						getNewContent(false);

						break;
					}
					clearData();
				}else {
					MessageDialog.openError(new Shell(), "Error", "Please select the class that you want to analize");
				}
				
				
			}
		});
	}
	
	private void testService(Composite viewArea) {
		Button service = new Button(viewArea, SWT.PUSH);
		service.setText("Service");

		service.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {

				switch (event.type) {
				case SWT.Selection:
					ServiceImplementation serviceImpl = new ServiceImplementation();
					
					serviceImpl.getClassMetrics("C:\\Users\\psousasa\\Desktop\\Mestrado\\PA\\ISCDE\\TESTWORKSPACE\\pidesco.demo\\MetricsEditor.java", "MetricsEditor", viewArea);
					break;
				}

			}
		});
	}
	
	private void putData(TableItem item, int position, int totalLines) {
			item.setText(0, Constants.NAME_ROWS[position]);
			double average = 0;
			if (Constants.NAME_ROWS[position].endsWith(Constants.PACKAGES)) {
				item.setText(1, String.valueOf(numberOfPackages));
			} else if (Constants.NAME_ROWS[position].contains(Constants.CLASSES)) {
				item.setText(1, String.valueOf(numberOfClasses));
				average = (float) numberOfClasses / numberOfPackages;
			} else if (Constants.NAME_ROWS[position].endsWith(Constants.METHODS)) {
				item.setText(1, String.valueOf(numberOfMethods));
				average = (float) numberOfMethods / numberOfClasses;
			} else if (Constants.NAME_ROWS[position].endsWith(Constants.OVERRIDE)) {
				item.setText(1, String.valueOf(numberOfOverride));
				average = (float) numberOfOverride / numberOfMethods;
			} else if (Constants.NAME_ROWS[position].endsWith(Constants.PARAMETERS)) {
				item.setText(1, String.valueOf(numberOfParameters));
				average = (float) numberOfParameters / numberOfMethods;
			} else if (Constants.NAME_ROWS[position].endsWith(Constants.STATIC_ATTRIBUTES)) {
				item.setText(1, String.valueOf(numberOfStaticAttributes));
				average = (float) numberOfStaticAttributes / numberOfAttributes;
			} else if (Constants.NAME_ROWS[position].endsWith(Constants.ATTRIBUTES)) {
				item.setText(1, String.valueOf(numberOfAttributes));
				average = (float) numberOfAttributes / numberOfClasses;
			} else if (Constants.NAME_ROWS[position].endsWith(Constants.TOTAL_LINES)) {
				item.setText(1, String.valueOf(totalLines));
			}

			if (average <= 0) {
				item.setText(2, "-");
			} else {
				DecimalFormat df = new DecimalFormat("#.###");
				item.setText(2, String.valueOf(df.format(average)));

			}
	}
	
	// extension point for project and class dependendo do boolean : true -> project; false -> class
	private void getNewContent(boolean isProject) { 
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = reg	.getConfigurationElementsFor("pa.iscde.metrics.patricia.metricsExtension");
		for (int i = 0; i < elements.length; i++) {
			try {
				String labelNewMetric = elements[i].getAttribute("label");
				String nameNewMetric = elements[i].getAttribute("className");
				ExtensibilityMetric action = (ExtensibilityMetric) elements[i].createExecutableExtension("class");

				
				TableItem givenMetric;
				if (isProject) {
					givenMetric = new TableItem(tableProject, SWT.NONE);

					List<Double> metricValue = action.addMetricProject();

					givenMetric.setText(0, labelNewMetric);
					givenMetric.setText(1, String.valueOf(metricValue.get(0)));
					givenMetric.setText(2, String.valueOf(metricValue.get(1)));
				}
				else {
					double metricValue = action.addMetricClass();
					if (nameNewMetric.equals(className)) {
						givenMetric = new TableItem(tableProject, SWT.NONE);
						givenMetric.setText(0, labelNewMetric);
						givenMetric.setText(1, String.valueOf(metricValue));
					}
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
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
