package pt.iscte.pidesco.demo.ext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import pt.iscte.pidesco.demo.extensibility.DemoAction;

public class TestAction implements DemoAction {

	@Override
	public void run(Composite area) {
		new Label(area, SWT.NONE).setText("???");
	}
}
