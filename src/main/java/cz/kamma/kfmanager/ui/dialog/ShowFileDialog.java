package cz.kamma.kfmanager.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cz.kamma.kfmanager.util.Constants;
import cz.kamma.kfmanager.util.FileHelper;

public class ShowFileDialog extends Dialog {

	private Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"
	private StyledText styledText = null;
	private Button button = null;
	private String fileName;

	public ShowFileDialog(Shell parent, String fileName, int style) {
		super(parent, style);
		this.fileName = fileName;
	}

	public Object open() {
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
		return null;
	}

	/**
	 * This method initializes sShell
	 */
	private void createDialog() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.CENTER;
		gridData1.verticalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(396, 277));
		styledText = new StyledText(sShell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		styledText.setLayoutData(gridData);
		styledText.setText(FileHelper.getFileContent(fileName));
		button = new Button(sShell, SWT.NONE);
		button.setText("Close");
		button.setLayoutData(gridData1);
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				sShell.dispose();
			}
		});
	}

}
