package cz.kamma.kfmanager.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipOutputStream;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cz.kamma.kfmanager.games.samecolors.GameWindow;
import cz.kamma.kfmanager.preference.PreferenceManager;
import cz.kamma.kfmanager.ui.dialog.ExitDialog;
import cz.kamma.kfmanager.ui.dialog.FavoritesDialog;
import cz.kamma.kfmanager.ui.dialog.FindFilesDialog;
import cz.kamma.kfmanager.ui.dialog.MainPreferenceDialog;
import cz.kamma.kfmanager.ui.dialog.ProgressDialog;
import cz.kamma.kfmanager.ui.dialog.ShowFileDialog;
import cz.kamma.kfmanager.ui.dialog.TextDialog;
import cz.kamma.kfmanager.ui.viewer.TextFileEditor;
import cz.kamma.kfmanager.util.ArrayUtils;
import cz.kamma.kfmanager.util.ConfigHelper;
import cz.kamma.kfmanager.util.Constants;
import cz.kamma.kfmanager.util.FileHelper;
import cz.kamma.kfmanager.util.Images;
import cz.kamma.kfmanager.vo.FavoriteItemVO;
import cz.kamma.kfmanager.vo.FavoritesVO;
import cz.kamma.kfmanager.vo.FileItemVO;
import cz.kamma.kfmanager.vo.PanelItemsVO;
import cz.kamma.kfmanager.vo.ZipArchivVO;

public class MainWindow extends Constants {

    private Display display;
    Shell shell;
    ToolBar toolBar;
    Table leftPanel, rightPanel;
    TableViewer leftPanelViewer, rightPanelViewer;
    Label statusBar, leftLabel, rightLabel, leftBottomLabel, rightBottomLabel, commandPromptLabel;
    Combo commandPrompt;
    Button leftRootButton, rightRootButton;
    Menu leftRootMenu, rightRootMenu;
    Composite leftWin, rightWin;
    SashForm sform;
    MenuItem favoritesMenu;
    boolean lastFocusedLeft = true;
    public ProgressDialog progressDialog;
    boolean renameKeysPressed = false;

    FavoritesVO favorites = new FavoritesVO();
    Images images = new Images();
    protected boolean isDragging;
    protected boolean isDropping;

    public Shell open(Display display) {
	this.display = display;
	ConfigHelper conf = ConfigHelper.getInstance();
	int posx = conf.getProperty(APPLICATION_PROPERTIES_POS_X, 10);
	int posy = conf.getProperty(APPLICATION_PROPERTIES_POS_Y, 10);
	int width = conf.getProperty(APPLICATION_PROPERTIES_WIDTH, 900);
	int height = conf.getProperty(APPLICATION_PROPERTIES_HEIGHT, 600);
	createShell();
	createMenuBar();
	createToolBar();
	createFilePanels();
	createPromptBar();
	createCommandBar();
	display.addFilter(SWT.KeyDown, new GlobalEventListener());
	display.addFilter(SWT.MouseDown, new GlobalEventListener());
	display.addFilter(SWT.Traverse, new GlobalEventListener());
	shell.setSize(width, height);
	shell.setLocation(posx, posy);
	shell.open();
	leftPanelViewer.getTable().setFocus();
	commandPromptLabel.setText(leftLabel.getText() + ">");
	shell.addListener(SWT.Resize, new Listener() {
	    public void handleEvent(Event e) {
		if (!ConfigHelper.getInstance().getProperty(PreferenceManager.SETTINGS_PREFIX_LEFT + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Full").equalsIgnoreCase("full"))
		    leftPanel.getColumn(0).setWidth(leftLabel.getBounds().width-4);
		if (!ConfigHelper.getInstance().getProperty(PreferenceManager.SETTINGS_PREFIX_RIGHT + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Full").equalsIgnoreCase("full"))
		    rightPanel.getColumn(0).setWidth(rightLabel.getBounds().width-4);
	    }
	});
	if (!ConfigHelper.getInstance().getProperty(PreferenceManager.SETTINGS_PREFIX_LEFT + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Full").equalsIgnoreCase("full"))
	    leftPanel.getColumn(0).setWidth(leftLabel.getBounds().width-4);
	if (!ConfigHelper.getInstance().getProperty(PreferenceManager.SETTINGS_PREFIX_RIGHT + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Full").equalsIgnoreCase("full"))
	    rightPanel.getColumn(0).setWidth(rightLabel.getBounds().width-4);
	return shell;
    }

    void createShell() {
	shell = new Shell(display);
	shell.setText(APPLICATION_TITLE);
	images.loadAll(display);
	GridLayout layout = new GridLayout(1, false);
	shell.setLayout(layout);
	shell.addShellListener(new ShellListener() {
	    public void shellActivated(ShellEvent e) {
		// TODO Auto-generated method stub
	    }

	    public void shellClosed(ShellEvent e) {
		if (ConfigHelper.getInstance().getPropertyAsBoolean(PreferenceManager.SETTINGS_CONFIRM_ON_EXIT, true)) {
		    ExitDialog dialog = new ExitDialog(shell, SWT.NONE);
		    if (dialog.open() != SWT.YES) {
			e.doit = false;
		    }
		}
	    }

	    public void shellDeactivated(ShellEvent e) {
		// TODO Auto-generated method stub
	    }

	    public void shellDeiconified(ShellEvent e) {
		// TODO Auto-generated method stub

	    }

	    public void shellIconified(ShellEvent e) {
		// TODO Auto-generated method stub
	    }
	});
	shell.addDisposeListener(new DisposeListener() {
	    public void widgetDisposed(DisposeEvent e) {
		images.freeAll();
		storeAllSystemProperties();
	    }
	});
    }

    protected void storeAllSystemProperties() {
	ConfigHelper conf = ConfigHelper.getInstance();
	conf.setProperty(APPLICATION_PROPERTIES_LEFT_PATH, FileHelper.getNearestDirectory(leftLabel.getText().split(ZIP_FILE_AND_PATH_SEPARATOR)[0]));
	conf.setProperty(APPLICATION_PROPERTIES_RIGHT_PATH, FileHelper.getNearestDirectory(rightLabel.getText().split(ZIP_FILE_AND_PATH_SEPARATOR)[0]));
	conf.setProperty(APPLICATION_PROPERTIES_POS_X, shell.getBounds().x);
	conf.setProperty(APPLICATION_PROPERTIES_POS_Y, shell.getBounds().y);
	conf.setProperty(APPLICATION_PROPERTIES_WIDTH, shell.getBounds().width);
	conf.setProperty(APPLICATION_PROPERTIES_HEIGHT, shell.getBounds().height);
	conf.storeProperties();
    }

    void createMenuBar() {
	Menu bar = new Menu(shell, SWT.BAR);
	shell.setMenuBar(bar);

	MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
	fileItem.setText("File");
	fileItem.setMenu(createFileMenu());

	MenuItem settingsItem = new MenuItem(bar, SWT.CASCADE);
	settingsItem.setText("Settings");
	settingsItem.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		PreferenceManager manager = new PreferenceManager();
		MainPreferenceDialog d = new MainPreferenceDialog(shell, manager);
		d.create();
		d.getShell().setText(APPLICATION_NAME + " - preferences");
		if (d.open() == 0) {
		    setPanelPreferences(leftPanel, PreferenceManager.SETTINGS_PREFIX_LEFT);
		    setPanelPreferences(rightPanel, PreferenceManager.SETTINGS_PREFIX_RIGHT);
		    FileItemVO actFile = getSelectedFile(leftPanel);
		    refreshPanel(leftLabel.getText(), actFile.getAbsolutePath(), true);
		    actFile = getSelectedFile(rightPanel);
		    refreshPanel(rightLabel.getText(), actFile.getAbsolutePath(), false);
		}
	    }
	});

	favoritesMenu = new MenuItem(bar, SWT.CASCADE);
	favoritesMenu.setText("Favorites");
	createFavoritesMenu(favoritesMenu);

	MenuItem gamesItem = new MenuItem(bar, SWT.CASCADE);
	gamesItem.setText("Games");
	gamesItem.setMenu(createGamesMenu());

	MenuItem helpItem = new MenuItem(bar, SWT.RIGHT | SWT.CASCADE);
	helpItem.setText("Help");
	helpItem.setMenu(createHelpMenu());

    }

    Menu createFileMenu() {
	Menu bar = shell.getMenuBar();
	Menu menu = new Menu(bar);

	MenuItem item = new MenuItem(menu, SWT.PUSH);
	item.setText("Exit");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		exitApp();
	    }
	});
	return menu;
    }

    Menu createGamesMenu() {
	Menu bar = shell.getMenuBar();
	Menu menu = new Menu(bar);

	MenuItem item = new MenuItem(menu, SWT.PUSH);
	item.setText("Same colors");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		startGame();
	    }
	});
	return menu;
    }

    public void createFavoritesMenu(final MenuItem favoritesMenu) {
	Menu bar = shell.getMenuBar();
	Menu menu = new Menu(bar);

	MenuItem item = new MenuItem(menu, SWT.PUSH);
	item.setText("Add to Favorites");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		addToFavorites();
	    }
	});

	item = new MenuItem(menu, SWT.PUSH);
	item.setText("Organize Favorites");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		FavoritesDialog dialog = new FavoritesDialog(shell, 0);
		int res = dialog.open();
		if (res == SWT.OK) {
		    favorites.loadFavorites();
		    createFavoritesMenu(favoritesMenu);
		}
	    }
	});

	new MenuItem(menu, SWT.SEPARATOR);

	loadFavorites(menu);

	if (favoritesMenu.getMenu() != null)
	    favoritesMenu.getMenu().dispose();
	favoritesMenu.setMenu(menu);
    }

    private void loadFavorites(Menu menu) {
	for (Enumeration<FavoriteItemVO> en = favorites.getItems().elements(); en.hasMoreElements();) {
	    final FavoriteItemVO item = en.nextElement();
	    final MenuItem mi = new MenuItem(menu, SWT.PUSH);
	    mi.setText(item.getName());
	    mi.setData(item);
	    mi.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
		    refreshPanel(item.getPath(), null, lastFocusedLeft);
		}
	    });
	}
    }

    protected void addToFavorites() {
	FileItemVO file = getSelectedFile(lastFocusedLeft ? leftPanel : rightPanel);
	if (file == null)
	    return;

	final MenuItem mi = new MenuItem(favoritesMenu.getMenu(), SWT.PUSH);
	final FavoriteItemVO item = new FavoriteItemVO(file.getAbsolutePath(), file.getAbsolutePath());
	favorites.addItem(item);
	favorites.storeFavorites();
	mi.setText(item.getName());
	mi.setData(item);
	mi.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		refreshPanel(item.getPath(), null, lastFocusedLeft);
	    }
	});

    }

    private void startGame() {
	new GameWindow(shell);
    }

    Menu createHelpMenu() {
	Menu bar = shell.getMenuBar();
	Menu menu = new Menu(bar);

	MenuItem item = new MenuItem(menu, SWT.PUSH);
	item.setText("Shortcuts");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		ShowFileDialog dialog = new ShowFileDialog(shell, RESOURCE_DIRECTORY_RELATIVE + RESOURCE_SHORTCUTS_FILE, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.open();
	    }
	});

	new MenuItem(menu, SWT.SEPARATOR);

	item = new MenuItem(menu, SWT.PUSH);
	item.setText("About");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		ShowFileDialog dialog = new ShowFileDialog(shell, RESOURCE_DIRECTORY_RELATIVE + RESOURCE_ABOUT_FILE, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.open();
	    }
	});
	return menu;
    }

    protected void exitApp() {
	shell.close();
    }

    private Menu createPopUpMenu() {
	Menu popUpMenu = new Menu(shell, SWT.POP_UP);

	popUpMenu.addMenuListener(new MenuAdapter() {
	    public void menuShown(MenuEvent e) {
		Menu menu = (Menu) e.widget;
		MenuItem[] items = menu.getItems();
		int count = getSelectedFiles(lastFocusedLeft).length;
		items[0].setEnabled(count == 1); // open
		items[1].setEnabled(count == 1 && !getSelectedFiles(lastFocusedLeft)[0].isDirectory()); // view
		items[2].setEnabled(count == 1 && !getSelectedFiles(lastFocusedLeft)[0].isDirectory()); // edit
		items[4].setEnabled(count > 0); // copy
		items[5].setEnabled(count > 0); // move
		items[6].setEnabled(count > 0); // delete
		items[8].setEnabled(count > 0); // favorites
	    }
	});

	// 0 Open
	MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
	item.setText("Open");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		FileItemVO[] items = getSelectedFiles(lastFocusedLeft);
		if (FILE_TYPE_DIR.equals(items[0].getType())) {
		    refreshPanel(items[0], leftLabel.getText(), true);
		} else if (FILE_TYPE_FILE.equals(items[0].getType()) && !items[0].isInZip()) {
		    FileHelper.executeFile(shell, items[0]);
		}
	    }
	});

	// 1 View
	item = new MenuItem(popUpMenu, SWT.CASCADE);
	item.setText("View");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		FileItemVO[] items = getSelectedFiles(lastFocusedLeft);
		commandViewFile(items[0]);
	    }
	});

	// 2 Edit
	item = new MenuItem(popUpMenu, SWT.CASCADE);
	item.setText("Edit");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		FileItemVO[] items = getSelectedFiles(lastFocusedLeft);
		commandEditFile(items[0]);
	    }
	});

	// 3 separator
	new MenuItem(popUpMenu, SWT.SEPARATOR);

	// 4 Copy
	item = new MenuItem(popUpMenu, SWT.CASCADE);
	item.setText("Copy");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		FileItemVO[] items = getSelectedFiles(lastFocusedLeft);
		commandCopy(items, lastFocusedLeft ? rightLabel.getText() : leftLabel.getText());
	    }
	});

	// 5 Move
	item = new MenuItem(popUpMenu, SWT.CASCADE);
	item.setText("Move");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		FileItemVO[] items = getSelectedFiles(lastFocusedLeft);
		commandMove(items, lastFocusedLeft ? rightLabel.getText() : leftLabel.getText());
	    }
	});

	// 6 Delete
	item = new MenuItem(popUpMenu, SWT.CASCADE);
	item.setText("Delete");
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		FileItemVO[] items = getSelectedFiles(lastFocusedLeft);
		commandDelete(items);
	    }
	});

	// 7 separator
	new MenuItem(popUpMenu, SWT.SEPARATOR);
	// 8 favorites
	item = new MenuItem(popUpMenu, SWT.CASCADE);
	item.setText("Add to Favorites");
	// popup.setMenu(favoritesMenu.getMenu());
	item.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		addToFavorites();
	    }
	});

	return popUpMenu;
    }

    void createFilePanels() {

	GridData spec = new GridData();
	spec.horizontalAlignment = GridData.FILL;
	spec.grabExcessHorizontalSpace = true;
	spec.verticalAlignment = GridData.FILL;
	spec.grabExcessVerticalSpace = true;

	sform = new SashForm(shell, SWT.HORIZONTAL);
	sform.setLayoutData(spec);

	createPanel(true);

	createPanel(false);

	leftRootMenu = new Menu(shell, SWT.POP_UP);
	leftRootMenu.addMenuListener(new MenuListener() {
	    public void menuHidden(MenuEvent e) {
		(lastFocusedLeft ? leftPanel : rightPanel).setFocus();
	    }

	    public void menuShown(MenuEvent e) {
	    }
	});
	rightRootMenu = new Menu(shell, SWT.POP_UP);
	rightRootMenu.addMenuListener(new MenuListener() {
	    public void menuHidden(MenuEvent e) {
		(lastFocusedLeft ? leftPanel : rightPanel).setFocus();
	    }

	    public void menuShown(MenuEvent e) {
	    }
	});

	String[] roots = FileHelper.getRoots(true);

	MenuItem item;
	for (int i = 0; i < roots.length; i++) {
	    final String drive = roots[i];
	    item = new MenuItem(leftRootMenu, SWT.CASCADE);
	    item.setText(drive);
	    item.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
		    refreshPanel(drive, null, true);
		    leftPanelViewer.getTable().setFocus();
		    leftRootButton.setText(drive);
		}
	    });
	}

	for (int i = 0; i < roots.length; i++) {
	    final String drive = roots[i];
	    item = new MenuItem(rightRootMenu, SWT.CASCADE);
	    item.setText(drive);
	    item.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
		    refreshPanel(drive, null, false);
		    rightPanelViewer.getTable().setFocus();
		    rightRootButton.setText(drive);
		}
	    });
	}

	ConfigHelper props = ConfigHelper.getInstance();
	String left = props.getProperty(APPLICATION_PROPERTIES_LEFT_PATH, FileHelper.getDafaultStartPath());
	String right = props.getProperty(APPLICATION_PROPERTIES_RIGHT_PATH, FileHelper.getDafaultStartPath());

	if (!FileHelper.isPathExists(left))
	    left = FileHelper.getDafaultStartPath();

	if (!FileHelper.isPathExists(right))
	    right = FileHelper.getDafaultStartPath();

	refreshPanel(left, null, true);
	refreshPanel(right, null, false);

	String leftRoot = FileHelper.getFileRoot(left);
	String rightRoot = FileHelper.getFileRoot(right);

	leftRootButton.setText(leftRoot);
	rightRootButton.setText(rightRoot);

	createButtonBarDropTarget(toolBar);

    }

    private void createPanel(final boolean createLeftPanel) {
	GridLayout winLayout = new GridLayout(4, true);
	winLayout.horizontalSpacing = 0;
	winLayout.verticalSpacing = 0;

	GridData panelLayout = new GridData();
	panelLayout.horizontalSpan = 4;
	panelLayout.horizontalAlignment = GridData.FILL;
	panelLayout.grabExcessHorizontalSpace = true;
	panelLayout.verticalAlignment = GridData.FILL;
	panelLayout.grabExcessVerticalSpace = true;

	GridData labelLayout = new GridData();
	labelLayout.horizontalSpan = 4;
	labelLayout.horizontalAlignment = GridData.FILL;
	labelLayout.grabExcessHorizontalSpace = true;

	GridData rootSelectLayout = new GridData();
	rootSelectLayout.horizontalSpan = 4;

	if ((createLeftPanel ? leftWin : rightWin) != null)
	    (createLeftPanel ? leftWin : rightWin).dispose();

	if (createLeftPanel)
	    leftWin = new Composite(sform, SWT.BORDER);
	else
	    rightWin = new Composite(sform, SWT.BORDER);

	(createLeftPanel ? leftWin : rightWin).setLayout(winLayout);

	if (createLeftPanel)
	    leftRootButton = new Button(leftWin, SWT.NONE);
	else
	    rightRootButton = new Button(rightWin, SWT.NONE);
	(createLeftPanel ? leftRootButton : rightRootButton).setLayoutData(rootSelectLayout);
	(createLeftPanel ? leftRootButton : rightRootButton).addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		Point loc = getPopupLocation(createLeftPanel);
		(createLeftPanel ? leftRootMenu : rightRootMenu).setLocation(loc.x, loc.y);
		(createLeftPanel ? leftRootMenu : rightRootMenu).setVisible(true);
	    }
	});

	if (createLeftPanel)
	    leftLabel = new Label(leftWin, SWT.BORDER);
	else
	    rightLabel = new Label(rightWin, SWT.BORDER);
	(createLeftPanel ? leftLabel : rightLabel).setBackground(COLOR_BLUE);
	(createLeftPanel ? leftLabel : rightLabel).setForeground(COLOR_WHITE);
	(createLeftPanel ? leftLabel : rightLabel).setFont(FONT_BOLD);
	(createLeftPanel ? leftLabel : rightLabel).setLayoutData(labelLayout);

	if (createLeftPanel)
	    leftPanel = new Table(leftWin, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
	else
	    rightPanel = new Table(rightWin, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
	(createLeftPanel ? leftPanel : rightPanel).setMenu(createPopUpMenu());
	(createLeftPanel ? leftPanel : rightPanel).setLayoutData(panelLayout);
	(createLeftPanel ? leftPanel : rightPanel).addFocusListener(new FocusListener() {
	    public void focusGained(FocusEvent e) {
		lastFocusedLeft = createLeftPanel;
		(createLeftPanel ? leftLabel : rightLabel).setBackground(COLOR_BLUE);
	    }

	    public void focusLost(FocusEvent e) {
		(createLeftPanel ? leftLabel : rightLabel).setBackground(COLOR_LIGHTBLUE);
	    }
	});

	setPanelPreferences((createLeftPanel ? leftPanel : rightPanel), createLeftPanel ? PreferenceManager.SETTINGS_PREFIX_LEFT : PreferenceManager.SETTINGS_PREFIX_RIGHT);

	(createLeftPanel ? leftPanel : rightPanel).pack();

	(createLeftPanel ? leftPanel : rightPanel).addMouseListener(new MouseListener() {
	    public void mouseDoubleClick(MouseEvent ev) {
		renameKeysPressed = false;
	    }

	    public void mouseDown(MouseEvent ev) {
		renameKeysPressed = false;
		if (ev.stateMask == SWT.CTRL) {
		    handleMouseSelectEventWithCTRL(ev);
		} else if (ev.stateMask == SWT.SHIFT) {
		    handleMouseSelectEventWithSHIFT(ev);
		}
	    }

	    public void mouseUp(MouseEvent ev) {
	    }
	});

	if (createLeftPanel)
	    leftPanelViewer = new TableViewer(leftPanel);
	else
	    rightPanelViewer = new TableViewer(rightPanel);
	(createLeftPanel ? leftPanelViewer : rightPanelViewer).setLabelProvider(new FileLabelProviderFull());
	(createLeftPanel ? leftPanelViewer : rightPanelViewer).setContentProvider(new FileContentProvider());
	(createLeftPanel ? leftPanelViewer : rightPanelViewer).setColumnProperties(COLUMN_NAMES);

	final CellEditor[] editors = new CellEditor[COLUMN_NAMES.length];
	for (int i = 0; i < COLUMN_NAMES.length; i++) {
	    if (i == 0) {
		final TextCellEditor tce = new TextCellEditor((createLeftPanel ? leftPanel : rightPanel), SWT.BORDER);
		tce.getControl().addKeyListener(new KeyListener() {
		    public void keyPressed(KeyEvent e) {
			if (e.keyCode == SWT.ESC)
			    tce.deactivate();
		    }

		    public void keyReleased(KeyEvent e) {
		    }
		});
		editors[i] = tce;
	    } else {
		editors[i] = null;
	    }
	}
	(createLeftPanel ? leftPanelViewer : rightPanelViewer).setCellEditors(editors);
	(createLeftPanel ? leftPanelViewer : rightPanelViewer).setCellModifier(new ICellModifier() {
	    public boolean canModify(Object element, String property) {
		if (element instanceof FileItemVO && renameKeysPressed) {
		    return true;
		}
		return false;
	    }

	    public Object getValue(Object element, String property) {
		FileItemVO item = (FileItemVO) element;
		return item.getName();
	    }

	    public void modify(Object element, String property, Object value) {
		TableItem titem = (TableItem) element;
		FileItemVO item = (FileItemVO) titem.getData();
		String newName = ((String) value).trim();
		if (newName.length() > 0) {
		    item.rename(newName);
		    refreshPanel((createLeftPanel ? leftLabel : rightLabel).getText(), item.getAbsolutePath(), createLeftPanel);
		    renameKeysPressed = false;
		}
	    }
	});

	// (createLeftPanel ? leftPanelViewer :
	// rightPanelViewer).setInput((createLeftPanel ? leftPanelItems :
	// rightPanelItems));
	(createLeftPanel ? leftPanelViewer : rightPanelViewer).setSorter(new FilePanelSorter(0));

	int colCount = ConfigHelper.getInstance().getProperty((createLeftPanel ? PreferenceManager.SETTINGS_PREFIX_LEFT : PreferenceManager.SETTINGS_PREFIX_RIGHT) + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Full").equalsIgnoreCase("full") ? COLUMN_NAMES.length : 1;
	;

	for (int i = 0; i < colCount; i++) {
	    final int ii = i;
	    (createLeftPanel ? leftPanelViewer : rightPanelViewer).getTable().getColumn(ii).addSelectionListener(new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
		    (createLeftPanel ? leftPanelViewer : rightPanelViewer).setSorter(new FilePanelSorter(ii));
		    (createLeftPanel ? leftPanelViewer : rightPanelViewer).refresh();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	    });
	}
	(createLeftPanel ? leftPanelViewer : rightPanelViewer).addSelectionChangedListener(new ISelectionChangedListener() {
	    public void selectionChanged(SelectionChangedEvent event) {
		FileItemVO item = getSelectedFile(event);
		if (item != null)
		    if (getPanelItems(lastFocusedLeft).getSelectedItems().length > 0)
			(createLeftPanel ? leftBottomLabel : rightBottomLabel).setText(getPanelItems(lastFocusedLeft).getSelectedItemsStatusText());
		    else
			(createLeftPanel ? leftBottomLabel : rightBottomLabel).setText(item.getStatusLine());
	    }
	});
	(createLeftPanel ? leftPanelViewer : rightPanelViewer).addOpenListener(new IOpenListener() {
	    public void open(OpenEvent event) {
		FileItemVO item = getSelectedFile(event);
		if (FILE_TYPE_DIR.equals(item.getType())) {
		    refreshPanel(item, (createLeftPanel ? leftLabel : rightLabel).getText(), createLeftPanel);
		} else if (FILE_TYPE_FILE.equals(item.getType()) && !item.isInZip()) {
		    FileHelper.executeFile(shell, item);
		}
	    }
	});

	createTableDragSource(createLeftPanel ? leftPanel : rightPanel);
	createTableDropTarget(createLeftPanel ? leftPanel : rightPanel);

	if (createLeftPanel)
	    leftBottomLabel = new Label(leftWin, SWT.NONE);
	else
	    rightBottomLabel = new Label(rightWin, SWT.NONE);

	(createLeftPanel ? leftBottomLabel : rightBottomLabel).setLayoutData(labelLayout);

    }

    protected void handleMouseSelectEventWithSHIFT(MouseEvent ev) {
    }

    protected void handleMouseSelectEventWithCTRL(MouseEvent ev) {
	FileItemVO file = getSelectedFile((Table) ev.widget);
	file.setSelected(!file.isSelected());
	(lastFocusedLeft ? leftPanelViewer : rightPanelViewer).refresh();
    }

    private void setPanelPreferences(Table panel, String preferencePrefix) {
	int colCount = ConfigHelper.getInstance().getProperty(preferencePrefix + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Full").equalsIgnoreCase("full") ? COLUMN_NAMES.length : 1;

	for (int i = 0; i < colCount; i++) {
	    TableColumn tc = new TableColumn(panel, SWT.LEFT);
	    tc.setText(COLUMN_NAMES[i]);
	    if (colCount == 1) {
		tc.setResizable(false);
	    } else
		tc.setWidth(COLUMN_WIDTHS[i]);
	}
	panel.setFont(new Font(shell.getDisplay(), PreferenceConverter.getFontData(PreferenceManager.getDefaultPreferenceStore(), preferencePrefix + PreferenceManager.SETTINGS_FONT)));
	panel.setHeaderVisible(ConfigHelper.getInstance().getPropertyAsBoolean(preferencePrefix + PreferenceManager.SETTINGS_SHOW_TABLE_HEADER, true));
	panel.setLinesVisible(ConfigHelper.getInstance().getPropertyAsBoolean(preferencePrefix + PreferenceManager.SETTINGS_SHOW_TABLE_LINES, false));
    }

    void createToolBar() {
	GridData toolBarLayout = new GridData();
	toolBarLayout.horizontalAlignment = GridData.FILL;
	toolBarLayout.grabExcessHorizontalSpace = true;
	toolBarLayout.verticalAlignment = GridData.FILL;
	toolBarLayout.grabExcessVerticalSpace = false;

	toolBar = new ToolBar(shell, SWT.NONE);
	toolBar.setLayoutData(toolBarLayout);

	ToolItem newItem = new ToolItem(toolBar, SWT.PUSH);
	newItem.setImage(images.getImageByResourceName("refresh"));
	newItem.setToolTipText("Refresh (F5)");
	newItem.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		String oldPath = getSelectedFile(lastFocusedLeft ? leftPanel : rightPanel).getAbsolutePath();
		refreshPanel(lastFocusedLeft ? leftLabel.getText() : rightLabel.getText(), oldPath, lastFocusedLeft);
	    }
	});

	new ToolItem(toolBar, SWT.SEPARATOR);

	newItem = new ToolItem(toolBar, SWT.PUSH);
	newItem.setImage(images.getImageByResourceName("brief"));
	newItem.setToolTipText("Brief view");
	newItem.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		ConfigHelper.getInstance().setProperty((lastFocusedLeft ? PreferenceManager.SETTINGS_PREFIX_LEFT : PreferenceManager.SETTINGS_PREFIX_RIGHT) + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Brief");
		if (!isBrief()) {
		    for (int i = COLUMN_NAMES.length; i > 0; i--) {
			try {
			    (lastFocusedLeft ? leftPanelViewer : rightPanelViewer).getTable().getColumn(i).dispose();
			} catch (Exception e) {

			}
		    }
		    (lastFocusedLeft ? leftPanelViewer : rightPanelViewer).getTable().getColumn(0).setWidth((lastFocusedLeft ? leftLabel : rightLabel).getBounds().width - 4);
		    (lastFocusedLeft ? leftPanelViewer : rightPanelViewer).getTable().getColumn(0).setResizable(false);
		    (lastFocusedLeft ? leftPanelViewer : rightPanelViewer).setLabelProvider(new FileLabelProviderBrief());
		}
	    }
	});

	newItem = new ToolItem(toolBar, SWT.PUSH);
	newItem.setImage(images.getImageByResourceName("full"));
	newItem.setToolTipText("Full view");
	newItem.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		ConfigHelper.getInstance().setProperty((lastFocusedLeft ? PreferenceManager.SETTINGS_PREFIX_LEFT : PreferenceManager.SETTINGS_PREFIX_RIGHT) + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Full");
		if (isBrief()) {
		    (lastFocusedLeft ? leftPanelViewer : rightPanelViewer).getTable().getColumn(0).setWidth(COLUMN_WIDTHS[0]);
		    (lastFocusedLeft ? leftPanelViewer : rightPanelViewer).getTable().getColumn(0).setResizable(true);
		    for (int i = 1; i < COLUMN_NAMES.length; i++) {
			TableColumn tc = new TableColumn((lastFocusedLeft ? leftPanel : rightPanel), SWT.NONE);
			tc.setText(COLUMN_NAMES[i]);
			tc.setWidth(COLUMN_WIDTHS[i]);
		    }
		    (lastFocusedLeft ? leftPanelViewer : rightPanelViewer).setLabelProvider(new FileLabelProviderFull());
		}
	    }
	});

    }

    protected boolean isBrief() {
	try {
	    (lastFocusedLeft ? leftPanelViewer : rightPanelViewer).getTable().getColumn(1).getText();
	    return false;
	} catch (Exception e) {
	}
	return true;
    }

    private void createPromptBar() {
	Composite promptBar = new Composite(shell, SWT.NONE);

	GridData promptBarLayout = new GridData();
	promptBarLayout.horizontalAlignment = GridData.FILL;
	promptBarLayout.grabExcessHorizontalSpace = true;
	promptBarLayout.verticalAlignment = GridData.FILL;
	promptBarLayout.grabExcessVerticalSpace = false;

	promptBar.setLayoutData(promptBarLayout);

	GridLayout layout = new GridLayout(3, true);
	layout.horizontalSpacing = 0;
	layout.verticalSpacing = 0;
	promptBar.setLayout(layout);

	GridData custLayout = new GridData();
	custLayout.horizontalAlignment = GridData.FILL;
	custLayout.grabExcessHorizontalSpace = true;
	custLayout.verticalAlignment = GridData.FILL;
	custLayout.grabExcessVerticalSpace = false;

	commandPromptLabel = new Label(promptBar, SWT.NONE);
	commandPromptLabel.setAlignment(SWT.RIGHT);
	commandPromptLabel.setLayoutData(custLayout);

	custLayout = new GridData();
	custLayout.horizontalAlignment = GridData.FILL;
	custLayout.horizontalSpan = 2;
	custLayout.grabExcessHorizontalSpace = true;
	custLayout.verticalAlignment = GridData.FILL;
	custLayout.grabExcessVerticalSpace = false;

	commandPrompt = new Combo(promptBar, SWT.DROP_DOWN);
	commandPrompt.setLayoutData(custLayout);
	commandPrompt.addKeyListener(new KeyListener() {
	    public void keyPressed(KeyEvent e) {
		if (e.keyCode == '\r')
		    handlePromptCommand();
	    }

	    public void keyReleased(KeyEvent e) {
	    }
	});
    }

    private void handlePromptCommand() {
	String command = commandPrompt.getText().trim();
	if (command == null || command.length() < 1)
	    return;
	String tmpCom = command.split(" ")[0];
	Arrays.sort(COMMANDS_FOR_MANUAL_PROCESSION);
	if (Arrays.binarySearch(COMMANDS_FOR_MANUAL_PROCESSION, tmpCom) > -1) {
	    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    box.setText("Not yet implemented");
	    box.setMessage("This command not yet implemented.");
	    box.open();
	    return;
	}
	// System.out.println("Running command: "+command);
	boolean addCommand = true;
	for (int i = 0; i < commandPrompt.getItemCount(); i++) {
	    if (commandPrompt.getItem(i).equals(command))
		addCommand = false;
	}
	if (addCommand)
	    commandPrompt.add(command);
	commandPrompt.cut();

	Program.launch(command);
    }

    private void createCommandBar() {
	Composite commandBar = new Composite(shell, SWT.NONE);

	GridData commandBarLayout = new GridData();
	commandBarLayout.horizontalAlignment = GridData.FILL;
	commandBarLayout.grabExcessHorizontalSpace = true;
	commandBarLayout.verticalAlignment = GridData.FILL;
	commandBarLayout.grabExcessVerticalSpace = false;

	commandBar.setLayoutData(commandBarLayout);

	GridLayout layout = new GridLayout(COMMAND_BUTTON_LABELS.length, true);
	layout.horizontalSpacing = 0;
	layout.verticalSpacing = 0;
	commandBar.setLayout(layout);

	GridData buttonLayout = new GridData();
	buttonLayout.horizontalAlignment = GridData.FILL;
	buttonLayout.grabExcessHorizontalSpace = true;
	buttonLayout.verticalAlignment = GridData.FILL;
	buttonLayout.grabExcessVerticalSpace = false;

	for (int i = 0; i < COMMAND_BUTTON_LABELS.length; i++) {
	    Button but = new Button(commandBar, SWT.NONE);
	    but.setLayoutData(buttonLayout);
	    but.setText(COMMAND_BUTTON_LABELS[i]);
	    but.addSelectionListener(new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
		    handleCommandButton(e);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	    });
	}
    }

    protected void handleCommandButton(SelectionEvent e) {
	if (e.getSource() instanceof Button) {
	    Button but = (Button) e.getSource();
	    int index = ArrayUtils.getIndexInArray(COMMAND_BUTTON_LABELS, but.getText()) + 1;
	    if (index == COMMAND_BUTTON_EXIT) {
		exitApp();
	    } else if (index == COMMAND_BUTTON_NEW_FOLDER) {
		commandCreateFolder();
	    } else if (index == COMMAND_BUTTON_COPY) {
		commandCopy(getSelectedFiles(lastFocusedLeft), lastFocusedLeft ? rightLabel.getText() : leftLabel.getText());
	    } else if (index == COMMAND_BUTTON_MOVE) {
		commandMove(getSelectedFiles(lastFocusedLeft), lastFocusedLeft ? rightLabel.getText() : leftLabel.getText());
	    } else if (index == COMMAND_BUTTON_EDIT) {
		commandEditFile(getSelectedFiles(lastFocusedLeft)[0]);
	    } else if (index == COMMAND_BUTTON_VIEW) {
		commandViewFile(getSelectedFiles(lastFocusedLeft)[0]);
	    } else if (index == COMMAND_BUTTON_DELETE) {
		commandDelete(getSelectedFiles(lastFocusedLeft));
	    }
	}
    }

    private void commandPackFiles(FileItemVO[] filesToCopy, String targetDir) {
	if (!isOperationAllowedForFile(lastFocusedLeft ? leftLabel.getText() : rightLabel.getText())) {
	    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    box.setText("Operation not allowed");
	    box.setMessage("This operation is not allowed.");
	    box.open();
	    return;
	}
	if (filesToCopy == null || filesToCopy.length < 1)
	    return;
	TextDialog dialog = new TextDialog(shell, "Pack files to:", targetDir + PATH_SEPARATOR + filesToCopy[0].getNameWithoutExt() + ".zip");
	String res = (String) dialog.open();
	if (res != null) {
	    // Open progress dialog
	    progressDialog = new ProgressDialog(shell, ProgressDialog.PACK);
	    progressDialog.setTotalWorkUnits(filesToCopy.length);
	    progressDialog.open();
	    ZipOutputStream zout = null;
	    try {
		zout = new ZipOutputStream(new FileOutputStream(res));
	    } catch (Exception e) {
		e.printStackTrace();
		return;
	    }
	    for (int i = 0; (i < filesToCopy.length) && (!progressDialog.isCancelled()); i++) {
		FileItemVO file = filesToCopy[i];
		progressDialog.setDetailFile(file.getAbsolutePath());
		while (!progressDialog.isCancelled()) {
		    if (FileHelper.packFileStructure(progressDialog, file, zout, lastFocusedLeft ? leftLabel.getText() : rightLabel.getText(), false)) {
			break;
		    } else if (!progressDialog.isCancelled()) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
			box.setText("Packing failed !");
			box.setMessage("Cannot pack file " + file.getAbsolutePath());
			int button = box.open();
			if (button == SWT.ABORT)
			    break;
			if (button == SWT.IGNORE)
			    i++;
		    }
		}
		progressDialog.addProgress(1);
	    }
	    try {
		zout.flush();
		zout.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    refreshPanel(rightLabel.getText(), rightLabel.getText(), false);
	    refreshPanel(leftLabel.getText(), leftLabel.getText(), true);
	    progressDialog.close();
	    progressDialog = null;
	}
    }

    private void commandMove(FileItemVO[] filesToCopy, String targetDir) {
	if (!isOperationAllowedForFile(lastFocusedLeft ? leftLabel.getText() : rightLabel.getText())) {
	    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    box.setText("Operation not allowed");
	    box.setMessage("This operation is not allowed.");
	    box.open();
	    return;
	}
	TextDialog dialog = new TextDialog(shell, "Move files to:", targetDir);
	String res = (String) dialog.open();
	if (res != null) {
	    // Open progress dialog
	    progressDialog = new ProgressDialog(shell, ProgressDialog.COPY);
	    progressDialog.setTotalWorkUnits(filesToCopy.length);
	    progressDialog.open();
	    for (int i = 0; (i < filesToCopy.length) && (!progressDialog.isCancelled()); i++) {
		FileItemVO file = filesToCopy[i];
		progressDialog.setDetailFile(file.getAbsolutePath());
		while (!progressDialog.isCancelled()) {
		    if (FileHelper.moveFileStructure(progressDialog, file.getFile(), new File(res), false)) {
			break;
		    } else if (!progressDialog.isCancelled()) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
			box.setText("Moving failed !");
			box.setMessage("Cannot move file " + file.getAbsolutePath());
			int button = box.open();
			if (button == SWT.ABORT)
			    break;
			if (button == SWT.IGNORE)
			    i++;
		    }
		}
		progressDialog.addProgress(1);
	    }

	    getPanelItems(lastFocusedLeft).deselectAll();
	    refreshPanel(rightLabel.getText(), null, false);
	    refreshPanel(leftLabel.getText(), null, true);
	    progressDialog.close();
	    progressDialog = null;
	}
    }

    private PanelItemsVO getPanelItems(boolean onLeftPanel) {
	if (onLeftPanel)
	    return (PanelItemsVO) leftPanelViewer.getInput();
	else
	    return (PanelItemsVO) rightPanelViewer.getInput();
    }

    private void commandCopy(FileItemVO[] filesToCopy, String targetDir) {
	TextDialog dialog = new TextDialog(shell, "Copy files to:", targetDir);
	String res = (String) dialog.open();
	if (res != null) {
	    // Open progress dialog
	    progressDialog = new ProgressDialog(shell, ProgressDialog.COPY);
	    progressDialog.setTotalWorkUnits(filesToCopy.length);
	    progressDialog.open();
	    for (int i = 0; (i < filesToCopy.length) && (!progressDialog.isCancelled()); i++) {
		FileItemVO file = filesToCopy[i];
		progressDialog.setDetailFile(file.getAbsolutePath());
		while (!progressDialog.isCancelled()) {
		    if (FileHelper.copyFileStructure(progressDialog, file, new File(res), false)) {
			break;
		    } else if (!progressDialog.isCancelled()) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
			box.setText("Copiing failed !");
			box.setMessage("Cannot copy file " + file.getAbsolutePath());
			int button = box.open();
			if (button == SWT.ABORT)
			    break;
			if (button == SWT.IGNORE)
			    i++;
		    }
		}
		progressDialog.addProgress(1);
	    }
	    refreshPanel((lastFocusedLeft ? rightLabel : leftLabel).getText(), (lastFocusedLeft ? rightLabel : leftLabel).getText(), !lastFocusedLeft);
	    progressDialog.close();
	    progressDialog = null;
	}
    }

    private void commandCreateFolder() {
	if (!isOperationAllowedForFile(lastFocusedLeft ? leftLabel.getText() : rightLabel.getText())) {
	    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    box.setText("Operation not allowed");
	    box.setMessage("This operation is not allowed.");
	    box.open();
	    return;
	}
	TextDialog dialog = new TextDialog(shell, "New Directory", null);
	String res = (String) dialog.open();
	if (res != null) {
	    String parent = lastFocusedLeft ? leftLabel.getText() : rightLabel.getText();
	    FileHelper.createDir(parent, res);
	    File tmp = new File(parent, res);
	    refreshPanel(parent, tmp.getAbsolutePath(), lastFocusedLeft);
	}
    }

    private void commandCreateFile() {
	if (!isOperationAllowedForFile(lastFocusedLeft ? leftLabel.getText() : rightLabel.getText())) {
	    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    box.setText("Operation not allowed");
	    box.setMessage("This operation is not allowed.");
	    box.open();
	    return;
	}
	TextDialog dialog = new TextDialog(shell, "New file name:", null);
	String res = (String) dialog.open();
	if (res != null) {
	    String parent = lastFocusedLeft ? leftLabel.getText() : rightLabel.getText();
	    if (FileHelper.createFile(parent, res)) {
		FileItemVO tmp = new FileItemVO(new File(parent + PATH_SEPARATOR + res));
		new TextFileEditor(shell, tmp, true);
		refreshPanel(parent, parent, lastFocusedLeft);
	    } else {
		MessageBox msgdialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		msgdialog.setText(Constants.APPLICATION_NAME);
		msgdialog.setMessage("Cannot create file.");
		msgdialog.open();
	    }
	}
    }

    private void commandRenameFile() {
	if (!isOperationAllowedForFile(lastFocusedLeft ? leftLabel.getText() : rightLabel.getText())) {
	    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    box.setText("Operation not allowed");
	    box.setMessage("This operation is not allowed.");
	    box.open();
	    return;
	}
	if (lastFocusedLeft) {
	    leftPanelViewer.editElement(leftPanelViewer.getElementAt(leftPanel.getSelectionIndex()), 0);
	} else {
	    rightPanelViewer.editElement(rightPanelViewer.getElementAt(rightPanel.getSelectionIndex()), 0);
	}
    }

    private void commandDelete(FileItemVO[] filesToDelete) {
	if (!isOperationAllowedForFile(lastFocusedLeft ? leftLabel.getText() : rightLabel.getText())) {
	    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    box.setText("Operation not allowed");
	    box.setMessage("This operation is not allowed.");
	    box.open();
	    return;
	}
	MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
	dialog.setText(Constants.APPLICATION_NAME);
	dialog.setMessage("Do you really want to delete selected files ?");
	int res = dialog.open();
	if (res == SWT.YES) {
	    int panelIndex = (lastFocusedLeft ? leftPanel : rightPanel).getSelectionIndex();
	    String parent = lastFocusedLeft ? leftLabel.getText() : rightLabel.getText();
	    // Open progress dialog
	    progressDialog = new ProgressDialog(shell, ProgressDialog.DELETE);
	    progressDialog.setTotalWorkUnits(filesToDelete.length);
	    progressDialog.open();
	    for (int i = 0; (i < filesToDelete.length) && (!progressDialog.isCancelled()); i++) {
		FileItemVO file = filesToDelete[i];
		progressDialog.setDetailFile(file.getName());
		while (!progressDialog.isCancelled()) {
		    if (FileHelper.deleteFileStructure(progressDialog, file.getFile())) {
			break;
		    } else if (!progressDialog.isCancelled()) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
			box.setText("Deleting failed !");
			box.setMessage("Cannot delete file " + file.getName());
			int button = box.open();
			if (button == SWT.ABORT)
			    i = filesToDelete.length;
			if (button == SWT.IGNORE)
			    break;
		    }
		}
		progressDialog.addProgress(1);
	    }
	    refreshPanel(parent, parent, lastFocusedLeft, panelIndex);
	    progressDialog.close();
	    progressDialog = null;
	}
    }

    public void commandViewFile(FileItemVO file) {
	if (!file.isDirectory()) {
	    new TextFileEditor(shell, file, false);
	}
    }

    public void commandEditFile(FileItemVO file) {
	if (!isOperationAllowedForFile(file.getAbsolutePath())) {
	    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
	    box.setText("Operation not allowed");
	    box.setMessage("This operation is not allowed.");
	    box.open();
	    return;
	}
	if (!file.isDirectory()) {
	    String editor = ConfigHelper.getInstance().getProperty(PreferenceManager.SETTINGS_EDITOR, null);
	    if (editor != null) {
		try {
		    String command = "\"" + editor + "\" " + file.getAbsolutePath();
		    // System.out.println(command);
		    Runtime.getRuntime().exec(command);
		} catch (Exception e) {
		    System.out.println("Cannot run external editor.");
		}
	    } else {
		new TextFileEditor(shell, file, true);
	    }
	    refreshPanel(file.getCurrentDir(), file.getAbsolutePath(), lastFocusedLeft);
	}
    }

    private boolean isOperationAllowedForFile(String filePath) {
	if (filePath == null || filePath.contains(ZIP_FILE_AND_PATH_SEPARATOR))
	    return false;
	return true;
    }

    private void refreshPanel(FileItemVO item, boolean refreshLeft) {
	refreshPanel(item.getCurrentDir(), item.getAbsolutePath(), refreshLeft);
    }

    public void refreshPanel(String parent, String oldPath, boolean refreshLeft) {
	refreshPanel(parent, oldPath, refreshLeft, -1);
    }

    public void refreshPanel(String parent, String oldPath, boolean refreshLeft, int panelIndex) {
	if (parent.indexOf(ZIP_FILE_AND_PATH_SEPARATOR) > -1) {
	    StringTokenizer strtok = new StringTokenizer(parent, ZIP_FILE_AND_PATH_SEPARATOR);
	    String zipFileName = strtok.nextToken();
	    String zipInternalPath = strtok.nextToken();
	    FileItemVO tmp = new FileItemVO(zipInternalPath, zipInternalPath, FILE_TYPE_DIR, 0, 0, true, zipFileName);
	    refreshPanel(tmp, (oldPath == null ? parent : oldPath), refreshLeft, panelIndex);
	} else {
	    if (FileHelper.isPathExists(parent))
		refreshPanel(new FileItemVO(new File(parent)), oldPath, refreshLeft, panelIndex);
	    else {
		MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
		box.setText("Error");
		box.setMessage("Path not exists. ('" + parent + "')");
		box.open();
	    }
	}

    }

    private void refreshPanel(FileItemVO item, String oldPath, boolean refreshLeft) {
	refreshPanel(item, oldPath, refreshLeft, -1);
    }

    private void refreshPanel(FileItemVO item, String oldPath, boolean refreshLeft, int panelIndex) {
	String parent = item.getAbsolutePath();
	FileItemVO[] files;

	if (item.isInZip()) {
	    ZipArchivVO zip = new ZipArchivVO(item.getZipFile());
	    files = zip.getFilesByPath(item.getAbsolutePath());
	    if (panelIndex >= files.length)
		panelIndex = files.length;
	    if (refreshLeft) {
		leftLabel.setText(item.getLabelTextForZipFile());
		PanelItemsVO tmp = new PanelItemsVO();
		tmp.setFileItems(files);
		leftPanelViewer.setInput(tmp);
		int selIndex = (panelIndex > -1 ? panelIndex : getItemIndexViaAbsolutePath(leftPanel, FileHelper.getZipPathFromLabel(oldPath)));
		leftPanelViewer.getTable().setSelection(selIndex);
		leftBottomLabel.setText(((FileItemVO) leftPanel.getItem(leftPanel.getSelectionIndex()).getData()).getStatusLine());
	    } else {
		rightLabel.setText(item.getLabelTextForZipFile());
		PanelItemsVO tmp = new PanelItemsVO();
		tmp.setFileItems(files);
		rightPanelViewer.setInput(tmp);
		int selIndex = (panelIndex > -1 ? panelIndex : getItemIndexViaAbsolutePath(rightPanel, FileHelper.getZipPathFromLabel(oldPath)));
		rightPanelViewer.getTable().setSelection(selIndex);
		rightBottomLabel.setText(((FileItemVO) rightPanel.getItem(rightPanel.getSelectionIndex()).getData()).getStatusLine());
	    }
	    zip.close();
	} else {
	    files = FileHelper.getFilesInDirectory(parent);
	    if (panelIndex >= files.length)
		panelIndex = files.length;
	    if (commandPrompt != null)
		commandPromptLabel.setText(parent + ">");
	    if (refreshLeft) {
		leftRootButton.setText(FileHelper.getDiskLabel(parent));
		leftLabel.setText(parent);
		PanelItemsVO tmp = new PanelItemsVO();
		tmp.addFileItem(FileHelper.getDirectoryParent(parent));
		tmp.addFileItems(files);
		leftPanelViewer.setInput(tmp);
		int selIndex = (panelIndex > -1 ? panelIndex : getItemIndexViaAbsolutePath(leftPanel, oldPath));
		leftPanelViewer.getTable().setSelection(selIndex);
		if (leftPanel.getSelectionIndex()>-1)
		    leftBottomLabel.setText(((FileItemVO) leftPanel.getItem(leftPanel.getSelectionIndex()).getData()).getStatusLine());
	    } else {
		rightRootButton.setText(FileHelper.getDiskLabel(parent));
		rightLabel.setText(parent);
		PanelItemsVO tmp = new PanelItemsVO();
		tmp.addFileItem(FileHelper.getDirectoryParent(parent));
		tmp.addFileItems(files);
		rightPanelViewer.setInput(tmp);
		int selIndex = (panelIndex > -1 ? panelIndex : getItemIndexViaAbsolutePath(rightPanel, oldPath));
		rightPanelViewer.getTable().setSelection(selIndex);
		if (rightPanel.getSelectionIndex()>-1)
		    rightBottomLabel.setText(((FileItemVO) rightPanel.getItem(rightPanel.getSelectionIndex()).getData()).getStatusLine());
	    }
	}
    }

    private int getItemIndexViaAbsolutePath(Table panel, String oldPath) {
	if (oldPath == null)
	    return 0;
	int index = 0;
	oldPath = oldPath.replace(BACKSLASH_CHAR, ZIP_PATH_SEPARATOR_CHAR).split(ZIP_FILE_AND_PATH_SEPARATOR)[0];
	for (int i = 0; i < panel.getItemCount(); i++) {
	    FileItemVO tmp = (FileItemVO) panel.getItem(i).getData();
	    if (tmp.getAbsolutePath().trim().replace(BACKSLASH_CHAR, ZIP_PATH_SEPARATOR_CHAR).equalsIgnoreCase(oldPath.trim()))
		return i;
	}
	return index;
    }

    private FileItemVO getSelectedFile(EventObject event) {
	Object element = null;
	if (event instanceof SelectionChangedEvent) {
	    IStructuredSelection selection = (IStructuredSelection) ((SelectionChangedEvent) event).getSelection();
	    if (selection.isEmpty()) {
		return null;
	    }
	    element = selection.getFirstElement();
	} else if (event instanceof OpenEvent) {
	    IStructuredSelection selection = (IStructuredSelection) ((OpenEvent) event).getSelection();
	    if (selection.isEmpty()) {
		return null;
	    }
	    element = selection.getFirstElement();
	}

	if (element instanceof TableItem) { // workaround for bug in older
	    // versions of Eclipse
	    element = ((TableItem) element).getData();
	}
	FileItemVO file = (FileItemVO) element;
	return file;
    }

    private FileItemVO getSelectedFile(Table table) {
	TableItem element = table.getItem(table.getSelectionIndex());
	FileItemVO file = (FileItemVO) element.getData();
	return file;
    }

    private FileItemVO[] getSelectedFiles(boolean onLeftPanel) {
	FileItemVO[] files = null;
	files = getPanelItems(onLeftPanel).getSelectedItems();

	if (files == null || files.length == 0) {
	    files = new FileItemVO[1];
	    files[0] = (FileItemVO) (lastFocusedLeft ? leftPanel.getSelection()[0].getData() : rightPanel.getSelection()[0].getData());
	}
	return files;
    }

    /**
     * Helper class that provides content for the table widget.
     */
    protected class FileContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
	    List<Object> result = new ArrayList<Object>();
	    for (Iterator i = ((PanelItemsVO) inputElement).getFileItems().iterator(); i.hasNext();) {
		result.add(i.next());
	    }
	    return result.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
    }

    /**
     * Helper class that knows how to display a table widget row for setting
     * Full.
     */
    protected class FileLabelProviderFull extends LabelProvider implements ITableLabelProvider, IColorProvider {

	public Image getColumnImage(Object element, int columnIndex) {
	    if (columnIndex == 0) {
		if (element instanceof FileItemVO) {
		    FileItemVO file = (FileItemVO) element;
		    return file.getIconFileByExt();
		}
	    }
	    return null;
	}

	public String getColumnText(Object element, int columnIndex) {
	    if (element instanceof FileItemVO) {
		FileItemVO file = (FileItemVO) element;

		switch (columnIndex) {
		case 0:
		    // return file.getName();
		    return file.isDirectory() ? "[" + file.getName() + "]" : file.getName();
		case 1:
		    return FileHelper.getSizeAsText(file.getSize());
		case 2:
		    return file.getTypeByExt() == null ? "" : file.getTypeByExt();
		case 3:
		    return FILE_DATE_FORMAT.format(new Date(file.getModified()));
		}
	    }
	    return null;
	}

	public Color getForeground(Object element) {
	    if (element instanceof FileItemVO) {
		FileItemVO file = (FileItemVO) element;
		if (file.isSelected())
		    return COLOR_RED;
	    }
	    return null;
	}

	public Color getBackground(Object element) {
	    return null;
	}
    }

    /**
     * Helper class that knows how to display a table widget row for setting
     * Brief.
     */
    protected class FileLabelProviderBrief extends LabelProvider implements ITableLabelProvider, IColorProvider {

	public Image getColumnImage(Object element, int columnIndex) {
	    if (columnIndex == 0) {
		if (element instanceof FileItemVO) {
		    FileItemVO file = (FileItemVO) element;
		    return file.getIconFileByExt();
		}
	    }
	    return null;
	}

	public String getColumnText(Object element, int columnIndex) {
	    if (element instanceof FileItemVO) {
		FileItemVO file = (FileItemVO) element;

		switch (columnIndex) {
		case 0:
		    // return file.getName();
		    return file.isDirectory() ? "[" + file.getName() + "]" : file.getName();
		}
	    }
	    return null;
	}

	public Color getForeground(Object element) {
	    if (element instanceof FileItemVO) {
		FileItemVO file = (FileItemVO) element;
		if (file.isSelected())
		    return COLOR_RED;
	    }
	    return null;
	}

	public Color getBackground(Object element) {
	    return null;
	}
    }

    protected class GlobalEventListener implements Listener {
	public void handleEvent(Event event) {
	    if (event.keyCode == SWT.F5 && event.stateMask == SWT.ALT && event.widget instanceof Table) {
		commandPackFiles(getSelectedFiles(lastFocusedLeft), lastFocusedLeft ? rightLabel.getText() : leftLabel.getText());
	    } else if (event.widget instanceof Table && event.keyCode == SWT.F4 && event.stateMask == SWT.SHIFT) {
		commandCreateFile();
	    } else if (event.widget instanceof Table && event.keyCode == SWT.F6 && event.stateMask == SWT.SHIFT) {
		renameKeysPressed = true;
		commandRenameFile();
	    } else if (event.keyCode == SWT.F1 && event.stateMask == SWT.ALT) {
		Point loc = getPopupLocation(true);
		leftRootMenu.setLocation(loc.x, loc.y);
		leftRootMenu.setVisible(true);
	    } else if (event.keyCode == SWT.F2 && event.stateMask == SWT.ALT) {
		Point loc = getPopupLocation(false);
		rightRootMenu.setLocation(loc.x, loc.y);
		rightRootMenu.setVisible(true);
	    } else if (event.keyCode == SWT.F7 && event.stateMask == SWT.ALT) {
		handleFindFiles();
	    } else if (event.keyCode == 16777218 && event.stateMask == SWT.CTRL || (event.widget instanceof Table && event.keyCode >= 'A' && event.keyCode <= 'z' && event.stateMask == SWT.NONE)) {
		commandPrompt.setFocus();
		// commandPrompt.setText(""+(char)event.keyCode);
	    } else if (event.widget instanceof Table && event.keyCode == SWT.F7 && event.stateMask == SWT.NONE) {
		commandCreateFolder();
	    } else if (event.widget instanceof Table && event.stateMask == SWT.NONE && (event.keyCode == SWT.F8 || event.keyCode == SWT.DEL)) {
		commandDelete(getSelectedFiles(lastFocusedLeft));
	    } else if (event.widget instanceof Table && event.keyCode == SWT.F3 && event.stateMask == SWT.NONE) {
		commandViewFile(getSelectedFiles(lastFocusedLeft)[0]);
	    } else if (event.widget instanceof Table && event.keyCode == SWT.F4 && event.stateMask == SWT.NONE) {
		commandEditFile(getSelectedFiles(lastFocusedLeft)[0]);
	    } else if (event.widget instanceof Table && event.keyCode == SWT.F5 && event.stateMask == SWT.NONE) {
		commandCopy(getSelectedFiles(lastFocusedLeft), lastFocusedLeft ? rightLabel.getText() : leftLabel.getText());
	    } else if (event.widget instanceof Table && event.keyCode == SWT.F6 && event.stateMask == SWT.NONE) {
		commandMove(getSelectedFiles(lastFocusedLeft), lastFocusedLeft ? rightLabel.getText() : leftLabel.getText());
	    } else if (event.keyCode == SWT.ESC && !(event.widget instanceof Text)) {
		(lastFocusedLeft ? leftPanel : rightPanel).forceFocus();
	    } else if (event.keyCode == 9) {
		if (lastFocusedLeft) {
		    rightPanel.forceFocus();
		    if (rightPanel.getSelectionIndex() < 0)
			rightPanel.select(0);
		} else {
		    leftPanel.forceFocus();
		    if (leftPanel.getSelectionIndex() < 0)
			leftPanel.select(0);
		}
		commandPromptLabel.setText((lastFocusedLeft ? leftLabel : rightLabel).getText() + ">");
		event.doit = true;
		event.detail = SWT.TRAVERSE_NONE;
	    } else if (event.widget instanceof Table && event.keyCode == SWT.INSERT && event.stateMask == SWT.NONE) {
		if (lastFocusedLeft) {
		    FileItemVO tmp = (FileItemVO) leftPanel.getItem(leftPanel.getSelectionIndex()).getData();
		    tmp.setSelected(!tmp.isSelected());
		    leftPanelViewer.refresh();
		    if (leftPanel.getSelectionIndex() < leftPanel.getItemCount() - 1)
			leftPanel.setSelection(leftPanel.getSelectionIndex() + 1);
		    if (getPanelItems(lastFocusedLeft).getSelectedItems().length > 0)
			leftBottomLabel.setText(getPanelItems(lastFocusedLeft).getSelectedItemsStatusText());
		    else
			leftBottomLabel.setText(((FileItemVO) leftPanel.getItem(leftPanel.getSelectionIndex()).getData()).getStatusLine());
		} else {
		    FileItemVO tmp = (FileItemVO) rightPanel.getItem(rightPanel.getSelectionIndex()).getData();
		    tmp.setSelected(!tmp.isSelected());
		    rightPanelViewer.refresh();
		    if (rightPanel.getSelectionIndex() < rightPanel.getItemCount() - 1)
			rightPanel.setSelection(rightPanel.getSelectionIndex() + 1);
		    if (getPanelItems(lastFocusedLeft).getSelectedItems().length > 0)
			rightBottomLabel.setText(getPanelItems(lastFocusedLeft).getSelectedItemsStatusText());
		    else
			rightBottomLabel.setText(((FileItemVO) rightPanel.getItem(rightPanel.getSelectionIndex()).getData()).getStatusLine());
		}
	    } else if (event.widget instanceof Table && event.character == Constants.ZIP_PATH_SEPARATOR_CHAR && event.stateMask == SWT.NONE) {
		if (lastFocusedLeft) {
		    getPanelItems(lastFocusedLeft).deselectAll();
		    leftPanelViewer.refresh();
		} else {
		    getPanelItems(lastFocusedLeft).deselectAll();
		    rightPanelViewer.refresh();
		}
	    } else if (event.widget instanceof Table && event.character == '*' && event.stateMask == SWT.NONE) {
		if (lastFocusedLeft) {
		    getPanelItems(lastFocusedLeft).selectInverted();
		    leftPanelViewer.refresh();
		} else {
		    getPanelItems(lastFocusedLeft).selectInverted();
		    rightPanelViewer.refresh();
		}
	    } else if (event.widget instanceof Table && event.character == '+' && event.stateMask == SWT.NONE) {
		TextDialog dialog = new TextDialog(shell, "Select files by mask:", "*.*", true);
		String mask = (String) dialog.open();
		if (lastFocusedLeft) {
		    if (mask != null) {
			getPanelItems(lastFocusedLeft).selectByMask(mask);
			leftPanelViewer.refresh();
		    }
		} else {
		    if (mask != null) {
			getPanelItems(lastFocusedLeft).selectByMask(mask);
			rightPanelViewer.refresh();
		    }
		}
	    } else if (event.widget instanceof Table && event.character == '-' && event.stateMask == SWT.NONE) {
		TextDialog dialog = new TextDialog(shell, "Deselect files by mask:", "*.*", true);
		String mask = (String) dialog.open();
		if (lastFocusedLeft) {
		    if (mask != null) {
			getPanelItems(lastFocusedLeft).deselectByMask(mask);
			leftPanelViewer.refresh();
		    }
		} else {
		    if (mask != null) {
			getPanelItems(lastFocusedLeft).deselectByMask(mask);
			rightPanelViewer.refresh();
		    }
		}
	    } else if (event.widget instanceof Table && event.keyCode == SWT.PAGE_DOWN && event.stateMask == SWT.CTRL && event.type == SWT.KeyDown) {
		event.doit = false;
		browseZipFile();
	    }
	}
    }

    protected class FilePanelSorter extends ViewerSorter {
	int col = -1;

	public FilePanelSorter(int col) {
	    super();
	    this.col = col;
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
	    FileItemVO file1 = (FileItemVO) e1;
	    FileItemVO file2 = (FileItemVO) e2;

	    if (col == 0) {
		if (file1.isUpDir())
		    return -2;
		if (file1.isDirectory() && !file2.isDirectory())
		    return -1;
		if (!file1.isDirectory() && file2.isDirectory())
		    return 1;
		return file1.getName().compareToIgnoreCase(file2.getName());
	    }
	    if (col == 1) {
		if (file1.isUpDir())
		    return -2;
		if (file1.isDirectory() && !file2.isDirectory())
		    return -1;
		if (!file1.isDirectory() && file2.isDirectory())
		    return 1;
		return file1.getSize() >= file2.getSize() ? -1 : 1;
	    }
	    if (col == 2) {
		if (file1.isUpDir())
		    return -2;
		if (file1.isDirectory() && file2.isDirectory())
		    return file1.getName().compareToIgnoreCase(file2.getName());
		if (file1.isDirectory() && !file2.isDirectory())
		    return -1;
		if (!file1.isDirectory() && file2.isDirectory())
		    return 1;
		return file1.getExtension().compareToIgnoreCase(file2.getExtension());
	    }
	    if (col == 3) {
		if (file1.isUpDir())
		    return -2;
		if (file1.isDirectory() && file2.isDirectory())
		    return file1.getName().compareToIgnoreCase(file2.getName());
		if (file1.isDirectory() && !file2.isDirectory())
		    return -1;
		if (!file1.isDirectory() && file2.isDirectory())
		    return 1;
		return file1.getModified() >= file2.getModified() ? -1 : 1;
	    }
	    return 0;
	}

    }

    public void browseZipFile() {
	TableItem[] fileToBrowse = lastFocusedLeft ? leftPanel.getSelection() : rightPanel.getSelection();
	if (fileToBrowse != null && fileToBrowse.length > 0) {
	    FileItemVO file = (FileItemVO) fileToBrowse[0].getData();
	    if (file.isZipFile()) {
		(lastFocusedLeft ? leftLabel : rightLabel).setText(file.getAbsolutePath() + ZIP_FILE_AND_PATH_SEPARATOR + ZIP_PATH_SEPARATOR);
		ZipArchivVO zaVO = file.getZipArchivVO();
		PanelItemsVO tmp = new PanelItemsVO();
		tmp.setFileItems(zaVO.getFilesByPath(ZIP_PATH_SEPARATOR));
		(lastFocusedLeft ? leftPanelViewer : rightPanelViewer).setInput(tmp);
		(lastFocusedLeft ? leftPanelViewer : rightPanelViewer).getTable().setSelection(0);
		FileItemVO fileVO = (FileItemVO) (lastFocusedLeft ? leftPanel : rightPanel).getItem(0).getData();
		(lastFocusedLeft ? leftBottomLabel : rightBottomLabel).setText(fileVO.getStatusLine());
		zaVO.close();
	    }
	}
    }

    public Point getPopupLocation(boolean left) {
	Point res = new Point((left ? leftRootButton : rightRootButton).getLocation().x, (left ? leftRootButton : rightRootButton).getLocation().y);
	Composite parent = (left ? leftRootButton : rightRootButton).getParent();
	while (parent != null) {
	    res.x += parent.getLocation().x;
	    res.y = parent.getLocation().y;
	    parent = parent.getParent();
	}
	res.x += 5;
	res.y += 110;
	return res;
    }

    public void handleFindFiles() {
	FindFilesDialog findDialog = new FindFilesDialog(shell, SWT.NONE, lastFocusedLeft ? leftLabel.getText() : rightLabel.getText(), this);
	FileItemVO file = (FileItemVO) findDialog.open();
	if (file != null) {
	    refreshPanel(file, lastFocusedLeft);
	}
    }

    private DragSource createTableDragSource(final Table table) {
	DragSource dragSource = new DragSource(table, DND.DROP_COPY | DND.DROP_MOVE);
	dragSource.setTransfer(new Transfer[] { FileTransfer.getInstance() });
	dragSource.addDragListener(new DragSourceListener() {
	    TableItem[] dndSelection = null;
	    String[] sourceNames = null;

	    public void dragStart(DragSourceEvent event) {
		dndSelection = table.getSelection();
		sourceNames = null;
		event.doit = dndSelection.length > 0;
		isDragging = true;
	    }

	    public void dragFinished(DragSourceEvent event) {
		handleDragFinished(event, sourceNames);
		dndSelection = null;
		sourceNames = null;
		isDragging = false;
		// handleDeferredRefresh();
	    }

	    public void dragSetData(DragSourceEvent event) {
		if (dndSelection == null || dndSelection.length == 0)
		    return;
		if (!FileTransfer.getInstance().isSupportedType(event.dataType))
		    return;

		sourceNames = new String[dndSelection.length];
		for (int i = 0; i < dndSelection.length; i++) {
		    File file = ((FileItemVO) dndSelection[i].getData()).getFile();
		    sourceNames[i] = file.getAbsolutePath();
		}
		event.data = sourceNames;
	    }
	});
	return dragSource;
    }

    private DropTarget createButtonBarDropTarget(final ToolBar tb) {
	DropTarget dropTarget = new DropTarget(tb, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
	dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
	dropTarget.addDropListener(new DropTargetAdapter() {
	    public void drop(DropTargetEvent event) {
		String[] sourceNames = (String[]) event.data;
		if (sourceNames == null || sourceNames.length > 1)
		    event.detail = DND.DROP_NONE;
		if (event.detail == DND.DROP_NONE)
		    return;

		final FileItemVO fileItem = new FileItemVO(new File(sourceNames[0]));
		ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(fileItem.getIconFileByExt());
		item.setToolTipText(fileItem.getAbsolutePath());
		item.addSelectionListener(new SelectionAdapter() {
		    public void widgetSelected(SelectionEvent event) {
			if (FILE_TYPE_DIR.equals(fileItem.getType())) {
			    refreshPanel(fileItem, (lastFocusedLeft ? leftLabel : rightLabel).getText(), lastFocusedLeft);
			} else if (FILE_TYPE_FILE.equals(fileItem.getType()) && !fileItem.isInZip()) {
			    FileHelper.executeFile(shell, fileItem);
			}
		    }
		});
	    }

	});
	return dropTarget;
    }

    private DropTarget createTableDropTarget(final Table table) {
	DropTarget dropTarget = new DropTarget(table, DND.DROP_COPY | DND.DROP_MOVE);
	dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
	dropTarget.addDropListener(new DropTargetAdapter() {
	    public void dragEnter(DropTargetEvent event) {
		isDropping = true;
	    }

	    public void dragLeave(DropTargetEvent event) {
		isDropping = false;
		// handleDeferredRefresh();
	    }

	    public void dragOver(DropTargetEvent event) {
		dropTargetValidate(event, getTargetFile(event));
		// System.out.println(getTargetFile(event));
		event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
	    }

	    public void drop(DropTargetEvent event) {
		File targetFile = getTargetFile(event);
		if (dropTargetValidate(event, targetFile))
		    handleDrop(event, targetFile);
	    }

	    private File getTargetFile(DropTargetEvent event) {
		// Determine the target File for the drop
		TableItem item = table.getItem(table.toControl(new Point(event.x, event.y)));
		File targetFile = null;
		if (item == null) {
		    // We are over an unoccupied area of the table.
		    // If it is a COPY, we can use the table's root file.
		    DropTarget dt = (DropTarget) event.widget;
		    if (dt.getControl() == leftPanel)
			targetFile = new File(leftLabel.getText());
		    else if (dt.getControl() == rightPanel)
			targetFile = new File(rightLabel.getText());
		} else {
		    // We are over a particular item in the table, use the
		    // item's file
		    targetFile = ((FileItemVO) item.getData()).getFile();
		    if (targetFile.isFile())
			targetFile = new File(targetFile.getParent());
		}
		return targetFile;
	    }
	});
	return dropTarget;
    }

    private boolean dropTargetValidate(DropTargetEvent event, File targetFile) {
	if (targetFile != null && targetFile.isDirectory()) {
	    if (event.detail != DND.DROP_COPY && event.detail != DND.DROP_MOVE) {
		event.detail = DND.DROP_MOVE;
	    }
	} else {
	    event.detail = DND.DROP_NONE;
	}
	return event.detail != DND.DROP_NONE;
    }

    private void handleDragFinished(DragSourceEvent event, String[] sourceNames) {
	// System.out.println("Drag finnished");
    }

    private void handleDrop(DropTargetEvent event, File targetFile) {
	// Get dropped data (an array of filenames)
	if (!dropTargetValidate(event, targetFile))
	    return;
	String[] sourceNames = (String[]) event.data;
	if (sourceNames == null)
	    event.detail = DND.DROP_NONE;
	if (event.detail == DND.DROP_NONE)
	    return;

	FileItemVO[] items = new FileItemVO[sourceNames.length];
	for (int i = 0; i < sourceNames.length; i++) {
	    items[i] = new FileItemVO(new File(sourceNames[i]));
	}
	if (event.detail == DND.DROP_COPY) {
	    commandCopy(items, targetFile.getAbsolutePath());
	} else if (event.detail == DND.DROP_MOVE) {
	    commandMove(items, targetFile.getAbsolutePath());
	}

    }
}
