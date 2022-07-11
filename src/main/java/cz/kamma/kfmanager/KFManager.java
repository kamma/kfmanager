package cz.kamma.kfmanager;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cz.kamma.kfmanager.ui.MainWindow;

public class KFManager {

	public static void main(String[] args) {
		Display display = new Display();
		MainWindow mainWin = new MainWindow();
		Shell shell = mainWin.open(display);
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}

}
