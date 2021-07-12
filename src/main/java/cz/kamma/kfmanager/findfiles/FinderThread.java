package cz.kamma.kfmanager.findfiles;

import java.io.File;
import java.util.Enumeration;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import cz.kamma.kfmanager.ui.dialog.FindFilesDialog;
import cz.kamma.kfmanager.util.FileHelper;
import cz.kamma.kfmanager.vo.FileItemVO;
import cz.kamma.kfmanager.vo.SearchOptionsVO;

public class FinderThread extends Thread {

    public boolean isStopped = false;
    Table resTable;
    SearchOptionsVO options;
    FindFilesDialog dialog;
    public Exception resultException = null;

    public FinderThread(FindFilesDialog dialog, Table resTable, SearchOptionsVO options) {
	this.resTable = resTable;
	this.options = options;
	this.dialog = dialog;
    }

    public void run() {
	findFileInDir(options.getSearchFor(), options.getSearchIn());
	isStopped = true;
	setFindDialogToEnd();
    }

    private void findFileInDir(String searchFor, String searchIn) {
	if (isStopped)
	    return;

	// System.out.println("Searching in: "+searchIn);
	File ftmp = new File(searchIn);
	if (ftmp.exists() && ftmp.isDirectory()) {
	    FileItemVO[] files = FileHelper.getFilesInDirectory(searchIn);
	    for (int i = 0; i < files.length; i++) {
		String name = files[i].getName();
		if (options.isInZip() && FileHelper.isZipFile(files[i].getAbsolutePath())) {
		    findInZip(files[i], searchFor);
		}
		if (!options.isRegExp()) {
		    if (options.isCaseSensitive() && FileHelper.wildcardMatch(name, searchFor))
			addFindFileLine(files[i]);
		    else if (!options.isCaseSensitive() && FileHelper.wildcardMatch(name.toLowerCase(), searchFor.toLowerCase()))
			addFindFileLine(files[i]);
		} else if (options.isRegExp()) {
		    try {
			if (options.isCaseSensitive() && FileHelper.regExpMatch(name, searchFor))
			    addFindFileLine(files[i]);
			else if (!options.isCaseSensitive() && FileHelper.regExpMatch(name.toLowerCase(), searchFor.toLowerCase()))
			    addFindFileLine(files[i]);
		    } catch (PatternSyntaxException e) {
			isStopped = true;
			resultException = e;
		    }
		}
		if (files[i].isDirectory()) {
		    setStatusBar(files[i].getAbsolutePath());
		    findFileInDir(searchFor, files[i].getAbsolutePath());
		}
	    }
	}
    }

    private void findInZip(FileItemVO itemVO, String searchFor) {
	ZipFile zipFile;
	try {
	    zipFile = new ZipFile(itemVO.getAbsolutePath());
	} catch (Exception e) {
	    return;
	}
	for (Enumeration en = zipFile.entries(); en.hasMoreElements();) {
	    ZipEntry ze = (ZipEntry) en.nextElement();
	    String name = ze.getName();
	    if (!options.isRegExp()) {
		if (options.isCaseSensitive() && FileHelper.wildcardMatch(name, searchFor))
		    addFindFileLine(itemVO);
		else if (!options.isCaseSensitive() && FileHelper.wildcardMatch(name.toLowerCase(), searchFor.toLowerCase()))
		    addFindFileLine(itemVO);
	    } else if (options.isRegExp()) {
		try {
		    if (options.isCaseSensitive() && FileHelper.regExpMatch(name, searchFor))
			addFindFileLine(itemVO);
		    else if (!options.isCaseSensitive() && FileHelper.regExpMatch(name.toLowerCase(), searchFor.toLowerCase()))
			addFindFileLine(itemVO);
		} catch (PatternSyntaxException e) {
		    isStopped = true;
		    resultException = e;
		}
	    }

	}
    }

    private void addFindFileLine(final FileItemVO file) {
	dialog.sShell.getDisplay().asyncExec(new Runnable() {
	    public void run() {
		TableItem tableItem = new TableItem(resTable, SWT.NONE);
		tableItem.setText(new String[] { file.getName(), file.getAbsolutePath() });
		tableItem.setData(file);
	    }
	});
    }

    private void setFindDialogToEnd() {
	dialog.sShell.getDisplay().asyncExec(new Runnable() {
	    public void run() {
		dialog.buttonOk.setText("Start Search");
		dialog.statusBar.setText("Search stopped");
		if (resultException != null)
		    dialog.showErrorWindow(resultException);
	    }
	});
    }

    private void setStatusBar(final String path) {
	dialog.sShell.getDisplay().asyncExec(new Runnable() {
	    public void run() {
		dialog.statusBar.setText(path);
	    }
	});
    }

}
