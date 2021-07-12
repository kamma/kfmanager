package cz.kamma.kfmanager.ui.dialog;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import cz.kamma.kfmanager.findfiles.FinderThread;
import cz.kamma.kfmanager.ui.MainWindow;
import cz.kamma.kfmanager.vo.FileItemVO;
import cz.kamma.kfmanager.vo.SearchOptionsVO;

public class FindFilesDialog extends Dialog {

    public Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"
    public Button buttonOk = null;
    private Label label = null;
    private Label label1 = null;
    private Combo searchForCombo = null;
    private Combo searchInCombo = null;
    private Button buttonCancel = null;
    private Button regExpCB = null;
    private Button inZipCB = null;
    private Button caseSensCB = null;
    private Table table = null;
    private Label label2 = null;
    private Label label3 = null;
    private Button buttonView = null;
    private Button buttonEdit = null;
    private Button buttonGoTo = null;
    private Button buttonFeedTo = null;
    public Label statusBar = null;
    private FinderThread fThread = null;

    private String initialPath = "";

    private FileItemVO result;
    private MainWindow mainWin;

    public FindFilesDialog(Shell sShell, int style, String initialPath, MainWindow mainWin) {
	super(sShell, style);
	this.initialPath = initialPath;
	this.mainWin = mainWin;
    }

    public Object open() {
	Shell parent = getParent();
	sShell = new Shell(parent, SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

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

    /**
     * This method initializes sShell
     */
    private void createDialog() {
	GridData gridData8 = new GridData();
	gridData8.horizontalSpan = 2;
	GridData gridData7 = new GridData();
	gridData7.horizontalSpan = 4;
	gridData7.horizontalAlignment = GridData.FILL;
	gridData7.verticalAlignment = GridData.FILL;
	gridData7.grabExcessVerticalSpace = true;
	gridData7.grabExcessHorizontalSpace = true;
	GridData gridData6 = new GridData();
	gridData6.horizontalSpan = 2;
	GridData gridData9 = new GridData();
	gridData9.horizontalSpan = 2;
	GridData gridData5 = new GridData();
	gridData5.horizontalSpan = 2;
	GridData gridData4 = new GridData();
	gridData4.horizontalAlignment = GridData.END;
	gridData4.verticalAlignment = GridData.CENTER;
	GridData gridData3 = new GridData();
	gridData3.horizontalAlignment = GridData.FILL;
	gridData3.verticalAlignment = GridData.CENTER;
	GridData gridData2 = new GridData();
	gridData2.horizontalAlignment = GridData.FILL;
	gridData2.verticalAlignment = GridData.CENTER;
	GridLayout gridLayout = new GridLayout();
	gridLayout.numColumns = 4;
	sShell.setText("Find files");
	sShell.setLayout(gridLayout);
	sShell.setSize(new Point(542, 353));
	label = new Label(sShell, SWT.NONE);
	label.setText("Search for:");
	createCombo();
	buttonOk = new Button(sShell, SWT.NONE);
	buttonOk.setText("Start Search");
	buttonOk.setLayoutData(gridData2);
	buttonOk.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		handleSearchButton();
	    }
	});
	new Label(sShell, SWT.NONE);
	label1 = new Label(sShell, SWT.NONE);
	label1.setText("Search in:");
	label1.setLayoutData(gridData4);
	createCombo1();
	buttonCancel = new Button(sShell, SWT.NONE);
	buttonCancel.setText("Cancel");
	buttonCancel.setLayoutData(gridData3);
	buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		handleCancelButton();
	    }
	});
	new Label(sShell, SWT.NONE);
	caseSensCB = new Button(sShell, SWT.CHECK);
	caseSensCB.setText(" Case sensitive");
	caseSensCB.setLayoutData(gridData5);
	new Label(sShell, SWT.NONE);
	new Label(sShell, SWT.NONE);
	regExpCB = new Button(sShell, SWT.CHECK);
	regExpCB.setText(" Regular expression");
	regExpCB.setLayoutData(gridData6);
	new Label(sShell, SWT.NONE);
	new Label(sShell, SWT.NONE);
	inZipCB = new Button(sShell, SWT.CHECK);
	inZipCB.setText(" Search in ZIP archives");
	inZipCB.setLayoutData(gridData9);
	new Label(sShell, SWT.NONE);
	new Label(sShell, SWT.NONE);
	label2 = new Label(sShell, SWT.NONE);
	label2.setText("");
	new Label(sShell, SWT.NONE);
	new Label(sShell, SWT.NONE);
	new Label(sShell, SWT.NONE);
	label3 = new Label(sShell, SWT.NONE);
	label3.setText("Search results:");
	label3.setLayoutData(gridData8);
	new Label(sShell, SWT.NONE);
	new Label(sShell, SWT.NONE);
	table = new Table(sShell, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
	table.setHeaderVisible(true);
	table.setLayoutData(gridData7);
	table.setLinesVisible(true);
	table.addKeyListener(new KeyListener() {

	    public void keyPressed(KeyEvent event) {
		if (event.keyCode == SWT.ESC) {
		    sShell.dispose();
		} else if (event.widget instanceof Table && event.keyCode == SWT.F3 && event.stateMask == SWT.NONE) {
		    TableItem element = table.getItem(table.getSelectionIndex());
		    FileItemVO file = (FileItemVO) element.getData();
		    if (file != null)
			mainWin.commandViewFile(file);
		} else if (event.widget instanceof Table && event.keyCode == SWT.F4 && event.stateMask == SWT.NONE) {
		    TableItem element = table.getItem(table.getSelectionIndex());
		    FileItemVO file = (FileItemVO) element.getData();
		    if (file != null)
			mainWin.commandEditFile(file);
		}
	    }

	    public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	    }

	});
	table.addSelectionListener(new SelectionListener() {

	    public void widgetDefaultSelected(SelectionEvent e) {
		TableItem element = table.getItem(table.getSelectionIndex());
		FileItemVO file = (FileItemVO) element.getData();
		result = file;
		sShell.dispose();
	    }

	    public void widgetSelected(SelectionEvent e) {
		TableItem element = table.getItem(table.getSelectionIndex());
		FileItemVO file = (FileItemVO) element.getData();
		if (file != null) {
		    if (!file.isDirectory()) {
			if (!file.isInZip())
			    buttonEdit.setEnabled(true);
			buttonView.setEnabled(true);
			buttonGoTo.setEnabled(true);
		    } else {
			buttonGoTo.setEnabled(true);
			buttonEdit.setEnabled(false);
			buttonView.setEnabled(false);
		    }
		}
	    }

	});
	TableLayout tableLayout = new TableLayout();
	tableLayout.addColumnData(new ColumnWeightData(1, 100, true));
	tableLayout.addColumnData(new ColumnWeightData(1, 100, true));

	table.setLayout(tableLayout);

	new TableColumn(table, SWT.NONE).setText("File Name");
	new TableColumn(table, SWT.NONE).setText("Path");

	table.pack();

	Composite commandBar = new Composite(sShell, SWT.NONE);

	GridData commandBarLayout = new GridData();
	commandBarLayout.horizontalSpan = 4;
	commandBarLayout.horizontalAlignment = GridData.FILL;
	commandBarLayout.grabExcessHorizontalSpace = true;
	commandBarLayout.verticalAlignment = GridData.FILL;
	commandBarLayout.grabExcessVerticalSpace = false;

	commandBar.setLayoutData(commandBarLayout);

	GridLayout layout = new GridLayout(4, true);
	layout.horizontalSpacing = 0;
	layout.verticalSpacing = 0;
	commandBar.setLayout(layout);

	GridData buttonLayout = new GridData();
	buttonLayout.horizontalAlignment = GridData.FILL;
	buttonLayout.grabExcessHorizontalSpace = true;
	buttonLayout.verticalAlignment = GridData.FILL;
	buttonLayout.grabExcessVerticalSpace = false;

	buttonView = new Button(commandBar, SWT.NONE);
	buttonView.setText("View");
	buttonView.setEnabled(false);
	buttonView.setLayoutData(buttonLayout);
	buttonView.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		TableItem element = table.getItem(table.getSelectionIndex());
		FileItemVO file = (FileItemVO) element.getData();
		if (file != null)
		    mainWin.commandViewFile(file);
	    }
	});
	buttonEdit = new Button(commandBar, SWT.NONE);
	buttonEdit.setText("Edit");
	buttonEdit.setEnabled(false);
	buttonEdit.setLayoutData(buttonLayout);
	buttonEdit.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		TableItem element = table.getItem(table.getSelectionIndex());
		FileItemVO file = (FileItemVO) element.getData();
		if (file != null)
		    mainWin.commandEditFile(file);
	    }
	});
	buttonGoTo = new Button(commandBar, SWT.NONE);
	buttonGoTo.setText("Go to");
	buttonGoTo.setEnabled(false);
	buttonGoTo.setLayoutData(buttonLayout);
	buttonGoTo.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		TableItem element = table.getItem(table.getSelectionIndex());
		FileItemVO file = (FileItemVO) element.getData();
		result = file;
		sShell.dispose();
	    }
	});
	buttonFeedTo = new Button(commandBar, SWT.NONE);
	buttonFeedTo.setText("Feed to panel");
	buttonFeedTo.setEnabled(false);
	buttonFeedTo.setLayoutData(buttonLayout);
	buttonFeedTo.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {

	    }
	});

	GridData statusBarLayout = new GridData();
	statusBarLayout.horizontalSpan = 4;
	statusBarLayout.horizontalAlignment = GridData.FILL;
	statusBarLayout.grabExcessHorizontalSpace = true;
	// statusBarLayout.verticalAlignment = GridData.FILL;
	statusBarLayout.grabExcessVerticalSpace = false;

	statusBar = new Label(sShell, SWT.BORDER);
	statusBar.setLayoutData(statusBarLayout);
	statusBar.setText("Search stopped");

    }

    protected void handleSearchButton() {
	if (validateSearchOptions()) {
	    if (fThread != null && !fThread.isStopped) {
		fThread.isStopped = true;
		buttonOk.setText("Start Search");
		statusBar.setText("Search stopped");
	    } else {
		table.removeAll();
		buttonOk.setText("Stop");
		fThread = new FinderThread(this, table, getOptions());
		fThread.start();
		statusBar.setText("Search in progress...");
	    }
	} else {
	    MessageBox box = new MessageBox(sShell, SWT.ICON_ERROR | SWT.OK);
	    box.setText("Error");
	    box.setMessage("'Search for' and 'Search in' are mandatory fields.");
	    box.open();
	}
    }

    private boolean validateSearchOptions() {
	if (searchForCombo.getText().length() < 1 || searchInCombo.getText().length() < 1)
	    return false;
	return true;
    }

    private SearchOptionsVO getOptions() {
	return new SearchOptionsVO(searchForCombo.getText(), searchInCombo.getText(), caseSensCB.getSelection(), regExpCB.getSelection(), inZipCB.getSelection());
    }

    protected void handleCancelButton() {
	if (fThread == null || fThread.isStopped) {
	    sShell.dispose();
	} else {
	    fThread.isStopped = true;
	    buttonOk.setText("Start Search");
	    statusBar.setText("Search stopped");
	}
    }

    /**
     * This method initializes combo
     * 
     */
    private void createCombo() {
	GridData gridData = new GridData();
	gridData.horizontalAlignment = GridData.FILL;
	gridData.grabExcessHorizontalSpace = true;
	gridData.verticalAlignment = GridData.CENTER;
	searchForCombo = new Combo(sShell, SWT.NONE);
	searchForCombo.setLayoutData(gridData);
	searchForCombo.addKeyListener(new KeyListener() {
	    public void keyPressed(KeyEvent e) {
		if (e.character == '\r')
		    handleSearchButton();
	    }

	    public void keyReleased(KeyEvent e) {
	    }
	});
    }

    /**
     * This method initializes combo1
     * 
     */
    private void createCombo1() {
	GridData gridData1 = new GridData();
	gridData1.horizontalAlignment = GridData.FILL;
	gridData1.grabExcessHorizontalSpace = true;
	gridData1.verticalAlignment = GridData.CENTER;
	searchInCombo = new Combo(sShell, SWT.NONE);
	searchInCombo.setLayoutData(gridData1);
	searchInCombo.setText(initialPath);
    }

    public void showErrorWindow(Exception e) {
	if (e == null)
	    return;
	MessageBox box = new MessageBox(sShell, SWT.ICON_ERROR | SWT.OK);
	box.setText("Error");
	box.setMessage("This is not valid regular expression.\n" + e.getMessage());
	box.open();
    }

}
