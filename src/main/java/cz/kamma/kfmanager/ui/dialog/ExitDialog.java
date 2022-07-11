package cz.kamma.kfmanager.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import cz.kamma.kfmanager.preference.PreferenceManager;
import cz.kamma.kfmanager.util.ConfigHelper;
import cz.kamma.kfmanager.util.Constants;

public class ExitDialog extends Dialog {

	private Shell sShell;
	private Label label = null;
	private Button buttonYes = null;
	private Button buttonNo = null;
	private Button showNextTime = null;

	int result;

	public ExitDialog(Shell parent, int style) {
		super(parent, style);
	}

	public ExitDialog(Shell parent, String labelText) {
		this(parent, 0);
	}

	public int open() {
		Shell parent = getParent();
		sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		sShell.setText(Constants.APPLICATION_NAME);

		createDialog();

		int dialogPosx = (parent.getLocation().x + parent.getSize().x / 2) - sShell.getSize().x / 2;
		int dialogPosy = (parent.getLocation().y + parent.getSize().y / 2) - sShell.getSize().y / 2;

		sShell.setLocation(dialogPosx, dialogPosy);

		sShell.open();
		Display display = parent.getDisplay();
		while (!sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	private void createDialog() {
		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.numColumns = 2;
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(200, 113));

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = false;

		label = new Label(sShell, SWT.NONE);
		label.setText("Do you really want to exit ?");
		label.setLayoutData(gridData);

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;

		buttonYes = new Button(sShell, SWT.NONE);
		buttonYes.setText("Yes");
		buttonYes.setLayoutData(gridData2);
		buttonYes.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				result = SWT.YES;
				ConfigHelper.getInstance().setProperty(PreferenceManager.SETTINGS_CONFIRM_ON_EXIT,
						showNextTime.getSelection() ? "true" : "false");
				sShell.dispose();
			}
		});
		buttonNo = new Button(sShell, SWT.NONE);
		buttonNo.setText("No");
		buttonNo.setLayoutData(gridData2);
		buttonNo.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				result = SWT.NO;
				ConfigHelper.getInstance().setProperty(PreferenceManager.SETTINGS_CONFIRM_ON_EXIT,
						showNextTime.getSelection() ? "true" : "false");
				sShell.dispose();
			}
		});
		showNextTime = new Button(sShell, SWT.CHECK);
		showNextTime.setText("Show next time ?");
		showNextTime.setSelection(
				ConfigHelper.getInstance().getPropertyAsBoolean(PreferenceManager.SETTINGS_CONFIRM_ON_EXIT, true));
		showNextTime.setLayoutData(gridData);
	}

}
