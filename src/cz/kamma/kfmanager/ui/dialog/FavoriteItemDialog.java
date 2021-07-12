package cz.kamma.kfmanager.ui.dialog;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import cz.kamma.kfmanager.vo.FavoriteItemVO;

public class FavoriteItemDialog extends Dialog {

    private Text nameText = null;
    private Text pathText = null;

    private FavoriteItemVO mAttribute = null;

    private String mTitle = "Edit";

    public FavoriteItemDialog(Shell parentShell) {
	super(parentShell);
	setShellStyle(getShellStyle());
    }

    /*
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
	getFavoriteItem().setName(nameText.getText());
	getFavoriteItem().setPath(pathText.getText());
	super.okPressed();
    }

    /*
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     */
    protected void cancelPressed() {
	super.cancelPressed();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
     */
    protected Control createDialogArea(Composite parent) {
	// Composite result = (Composite) super.createDialogArea(parent);

	Composite panel = new Composite(parent, SWT.NONE);
	panel.setLayout(new GridLayout(2, false));
	GridData gridPan = new GridData();
	gridPan.grabExcessVerticalSpace = true;
	gridPan.grabExcessHorizontalSpace = true;
	gridPan.minimumWidth = 300;
	gridPan.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL_BOTH;
	gridPan.verticalAlignment = org.eclipse.swt.layout.GridData.FILL_BOTH;
	panel.setLayoutData(gridPan);

	Label label = new Label(panel, SWT.NONE);
	label.setText("Name");
	nameText = new Text(panel, SWT.SINGLE | SWT.BORDER);
	nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	Label value = new Label(panel, SWT.NONE);
	value.setText("Path");
	pathText = new Text(panel, SWT.SINGLE | SWT.BORDER);
	pathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	initWidgetValues();
	hookListeners();

	nameText.forceFocus();

	return parent;
    }

    protected Control createContents(Composite parent) {
	Control result = super.createContents(parent);
	validateInput();
	return result;
    }

    private void initWidgetValues() {
	if (getFavoriteItem() == null) {
	    throw new IllegalStateException("Favorite Item not set; cannot initialize");
	}
	nameText.setText(getFavoriteItem().getName());
	pathText.setText(getFavoriteItem().getPath());
    }

    private void hookListeners() {

	nameText.addModifyListener(new ModifyListener() {
	    public void modifyText(ModifyEvent e) {
		validateInput();
	    }
	});
	pathText.addModifyListener(new ModifyListener() {
	    public void modifyText(ModifyEvent e) {
		validateInput();
	    }
	});
    }

    private void validateInput() {
	boolean hasName = nameText.getText().trim().length() > 0;
	boolean hasPath = pathText.getText().trim().length() > 0;

	boolean enabled = hasName && hasPath;
	getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }

    protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText(getTitle());
    }

    public String getTitle() {
	return mTitle;
    }

    public void setTitle(String title) {
	mTitle = title;
    }

    public FavoriteItemVO getFavoriteItem() {
	return mAttribute;
    }

    public void setFavoriteItem(FavoriteItemVO attribute) {
	mAttribute = attribute;
    }
}
