package cz.kamma.kfmanager.games.samecolors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GameWindow {

	private int WIDTH = 32;
	private int HEIGHT = 32;
	private Display display;
	private Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"
	private Canvas canvas = null;
	private Menu menuBar = null;
	private Engine en;
	private Label bottomLabel;
	private long start = 0L;
	private Image blue;
	private Image blue2;
	private Image red;
	private Image red2;
	private Image yellow;
	private Image yellow2;
	private int lastX, lastY = 0;

	public GameWindow(Shell parent) {
		display = parent.getDisplay();
		sShell = new Shell(parent);
		open();
		while (!sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public void open() {
		createSShell(sShell);
		sShell.open();
	}

	/**
	 * This method initializes canvas
	 *
	 */
	private void createCanvas() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		canvas = new Canvas(sShell, SWT.NO_BACKGROUND);
		canvas.setLayout(null);
		canvas.setLayoutData(gridData);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				fillTable(e);

			}
		});
		canvas.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				int x = e.x / WIDTH;
				int y = e.y / HEIGHT;
				if (x != lastX || y != lastY) {
					// System.out.println(x+":"+y);
					lastX = x;
					lastY = y;
					en.reduceField(y, x, true);
					canvas.redraw();
				}
			}
		});
		canvas.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				int x = e.x / WIDTH;
				int y = e.y / HEIGHT;
				// System.out.println(y + "," + x);
				en.reduceField(y, x, false);
				canvas.redraw();
				if (start == 0)
					start = System.currentTimeMillis();
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
		});
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell(Shell sShell) {
		en = new Engine();
		en.initialize();
		sShell.setText("Same colors");
		createCanvas();
		sShell.setSize(new Point(WIDTH * en.WIDTH + 16, HEIGHT * en.HEIGHT + 82));
		sShell.setLayout(new GridLayout());
		menuBar = new Menu(sShell, SWT.BAR);
		sShell.setMenuBar(menuBar);
		MenuItem fileMenu = new MenuItem(menuBar, SWT.CASCADE);
		fileMenu.setText("File");
		fileMenu.setMenu(createFileMenu());
		GridData labelLayout = new GridData();
		labelLayout.horizontalAlignment = GridData.FILL;
		labelLayout.grabExcessHorizontalSpace = true;
		bottomLabel = new Label(sShell, SWT.BORDER);
		bottomLabel.setLayoutData(labelLayout);
		blue = new Image(null, "./res/blue.png");
		blue2 = new Image(null, "./res/blue2.png");
		red = new Image(null, "./res/red.png");
		red2 = new Image(null, "./res/red2.png");
		yellow = new Image(null, "./res/yellow.png");
		yellow2 = new Image(null, "./res/yellow2.png");

	}

	Menu createFileMenu() {
		Menu bar = sShell.getMenuBar();
		Menu menu = new Menu(bar);

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("New");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				start = 0L;
				en.initialize();
				canvas.redraw();
			}
		});

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Exit");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sShell.close();
			}
		});

		return menu;
	}

	private void fillTable(PaintEvent e) {
		Image image = (Image) canvas.getData("double-buffer-image");
		if (image == null || image.getBounds().width != canvas.getSize().x
				|| image.getBounds().height != canvas.getSize().y) {
			image = new Image(display, canvas.getSize().x, canvas.getSize().y);
			canvas.setData("double-buffer-image", image);
		}
		GC imageGC = new GC(image);
		imageGC.setBackground(e.gc.getBackground());
		imageGC.setForeground(e.gc.getForeground());

		int[][] tmpField = en.getGameField();
		for (int y = 0; y < en.getHEIGHT(); y++) {
			for (int x = 0; x < en.getWIDTH(); x++) {
				if (tmpField[x][y] == 1)
					imageGC.drawImage(blue, y * WIDTH, x * HEIGHT);
				if (tmpField[x][y] == 2)
					imageGC.drawImage(red, y * WIDTH, x * HEIGHT);
				if (tmpField[x][y] == 3)
					imageGC.drawImage(yellow, y * WIDTH, x * HEIGHT);
				if (tmpField[x][y] == 4)
					imageGC.drawImage(blue2, y * WIDTH, x * HEIGHT);
				if (tmpField[x][y] == 5)
					imageGC.drawImage(red2, y * WIDTH, x * HEIGHT);
				if (tmpField[x][y] == 6)
					imageGC.drawImage(yellow2, y * WIDTH, x * HEIGHT);
				if (tmpField[x][y] == 0) {
					imageGC.setBackground(new Color(null, 0, 0, 0));
					imageGC.fillRectangle(y * WIDTH, x * HEIGHT, 32, 32);
				}
				// e.gc.fillRectangle(y * en.getWIDTH(), x * en.getHEIGHT(), 14,
				// 14);
			}
		}
		e.gc.drawImage(image, 0, 0);
		imageGC.dispose();

		int moves = en.getPossibleMoves();
		if (moves == 0 && start > 0L) {
			long gameTime = (System.currentTimeMillis() - start) / 1000L;
			bottomLabel.setText("Game Over");
			int fscore = en.getFinalScore(gameTime);
			start = 0L;
			MessageBox box = new MessageBox(sShell, SWT.ICON_INFORMATION | SWT.OK);
			box.setText("Game Over");
			box.setMessage(
					"Game Over\nNo more moves.\nYour final score is: " + fscore + "\nGame Time: " + gameTime + " sec");
			box.open();
			en.initialize();
			canvas.redraw();
		} else {
			bottomLabel.setText("Stones: " + en.getFieldCount() + " Moves: " + moves);
		}
	}

}
