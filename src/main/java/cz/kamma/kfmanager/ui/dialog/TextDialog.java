package cz.kamma.kfmanager.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cz.kamma.kfmanager.util.Constants;

public class TextDialog extends Dialog {

	private Shell sShell;
	private Label label = null;
	private Text text = null;
	private Button buttonOK = null;
	private Button buttonCancel = null;
	private boolean selectText = false;

	String result;
	private String initialText;
	private String labelText;

	public TextDialog(Shell parent, int style) {
		super(parent, style);
	}

	public TextDialog(Shell parent, String labelText, String initialText) {
		this(parent, labelText, initialText, true);
	}

	public TextDialog(Shell parent, String labelText, String initialText, boolean selectText) {
		this(parent, 0);
		this.initialText = initialText;
		this.labelText = labelText;
		this.selectText = selectText;
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
		return result;
	}

	public String getEnteredText() {
		return result;
	}

	private void createDialog() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 3;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = false;
		GridLayout gridLayout = new GridLayout(3, true);
		gridLayout.numColumns = 3;
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(300, 113));
		label = new Label(sShell, SWT.NONE);
		label.setText(labelText != null ? labelText : "");
		label.setLayoutData(gridData);
		text = new Text(sShell, SWT.BORDER);
		text.setLayoutData(gridData1);
		text.setText(initialText != null ? initialText : "");
		if (selectText)
			text.selectAll();
		text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					result = text.getText();
					sShell.dispose();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		new Label(sShell, SWT.NONE);
		buttonOK = new Button(sShell, SWT.NONE);
		buttonOK.setText("OK");
		buttonOK.setLayoutData(gridData2);
		buttonOK.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				result = text.getText();
				sShell.dispose();
			}
		});
		buttonCancel = new Button(sShell, SWT.NONE);
		buttonCancel.setText("Cancel");
		buttonCancel.setLayoutData(gridData3);
		buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				result = null;
				sShell.dispose();
			}
		});
	}

}
