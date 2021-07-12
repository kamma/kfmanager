package cz.kamma.kfmanager.ui.dialog;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;

import cz.kamma.kfmanager.preference.PreferenceManager;

public class MainPreferenceDialog extends PreferenceDialog {

    public MainPreferenceDialog(Shell shell, PreferenceManager manager) {
	super(shell, manager);
    }

    public void refresh() {
	getTreeViewer().refresh();
    }
}