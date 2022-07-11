package cz.kamma.kfmanager.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class ProgressDialog {
	public final static int COPY = 0;
	public final static int DELETE = 1;
	public final static int MOVE = 2;
	public final static int PACK = 3;

	Shell shell;
	Label messageLabel, detailLabel;
	ProgressBar progressBar;
	Button cancelButton;
	boolean isCancelled = false;
	Display display;

	final String operationKeyName[] = { "Copy", "Delete", "Move", "Pack" };

	/**
	 * Creates a progress dialog but does notopen it immediately.
	 *
	 * @param parent the parent Shell
	 * @param style  one of COPY, MOVE
	 */
	public ProgressDialog(Shell parent, int style) {
		display = parent.getDisplay();
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setText(operationKeyName[style]);
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				isCancelled = true;
			}
		});

		messageLabel = new Label(shell, SWT.HORIZONTAL);
		messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		messageLabel.setText(operationKeyName[style]);

		progressBar = new ProgressBar(shell, SWT.HORIZONTAL | SWT.WRAP);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		progressBar.setMinimum(0);
		progressBar.setMaximum(0);

		detailLabel = new Label(shell, SWT.HORIZONTAL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.widthHint = 400;
		detailLabel.setLayoutData(gridData);

		cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL));
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isCancelled = true;
				cancelButton.setEnabled(false);
			}
		});
	}

	/**
	 * Sets the detail text to show the filename along with a string representing
	 * the operation being performed on that file.
	 *
	 * @param file      the file to be detailed
	 * @param operation one of COPY, DELETE
	 */
	public void setDetailFile(String file) {
		detailLabel.setText(file);
	}

	/**
	 * Returns true if the Cancel button was been clicked.
	 *
	 * @return true if the Cancel button was clicked.
	 */
	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * Sets the total number of work units to be performed.
	 *
	 * @param work the total number of work units
	 */
	public void setTotalWorkUnits(int work) {
		progressBar.setMaximum(work);
	}

	/**
	 * Adds to the total number of work units to be performed.
	 *
	 * @param work the number of work units to add
	 */
	public void addWorkUnits(int work) {
		setTotalWorkUnits(progressBar.getMaximum() + work);
	}

	/**
	 * Sets the progress of completion of the total work units.
	 *
	 * @param work the total number of work units completed
	 */
	public void setProgress(int work) {
		progressBar.setSelection(work);
		while (display.readAndDispatch()) {
		} // enable event processing
	}

	/**
	 * Adds to the progress of completion of the total work units.
	 *
	 * @param work the number of work units completed to add
	 */
	public void addProgress(int work) {
		setProgress(progressBar.getSelection() + work);
	}

	/**
	 * Opens the dialog.
	 */
	public void open() {
		shell.pack();
		final Shell parentShell = (Shell) shell.getParent();
		Rectangle rect = parentShell.getBounds();
		Rectangle bounds = shell.getBounds();
		bounds.x = rect.x + (rect.width - bounds.width) / 2;
		bounds.y = rect.y + (rect.height - bounds.height) / 2;
		shell.setBounds(bounds);
		shell.open();
	}

	/**
	 * Closes the dialog and disposes its resources.
	 */
	public void close() {
		shell.close();
		shell.dispose();
		shell = null;
		messageLabel = null;
		detailLabel = null;
		progressBar = null;
		cancelButton = null;
	}
}
