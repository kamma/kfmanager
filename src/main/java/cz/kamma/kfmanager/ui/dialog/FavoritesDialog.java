package cz.kamma.kfmanager.ui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import cz.kamma.kfmanager.vo.FavoriteItemVO;
import cz.kamma.kfmanager.vo.FavoritesVO;

public class FavoritesDialog extends Dialog {

	private FavoritesVO favorites;

	private Shell sShell = null;

	private static final String[] COLUMN_NAMES = new String[] { "Name", "Path" };
	private static final int[] COLUMN_WIDTHS = new int[] { 150, 600 };

	private Table mTableWidget = null;
	private TableViewer mTableViewer = null;
	private Button mButtonAdd = null;
	private Button mButtonEdit = null;
	private Button mButtonRemove = null;
	private Button mButtonMoveUp = null;
	private Button mButtonMoveDown = null;
	private Button mButtonOK = null;
	private Button mButtonCancel = null;

	private int result;

	public FavoritesDialog(Shell sShell, int style) {
		super(sShell, style);
		this.favorites = new FavoritesVO();
	}

	public int open() {
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

	public void createDialog() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		sShell.setText("Organize Favorites");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(542, 353));

		createTableWidget();
		createButtonsPanel();
		createButtons2Panel();

		hookListeners();
		updateEnabledState();
		mButtonAdd.forceFocus();

	}

	/**
	 * Registers listeners with the widgets.
	 */
	private void hookListeners() {
		mButtonOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = SWT.OK;
				favorites.storeFavorites();
				sShell.dispose();
			}
		});
		mButtonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = SWT.CANCEL;
				sShell.dispose();
			}
		});
		mButtonAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAddPressed();
			}
		});
		mButtonEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEditPressed();
			}
		});
		mButtonRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemovePressed();
			}
		});
		mButtonMoveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleMoveUpPressed();
			}
		});
		mButtonMoveDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleMoveDownPressed();
			}
		});
		mTableWidget.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				handleEditPressed();
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
		});
	}

	protected void handleMoveDownPressed() {
		int pos = mTableWidget.getSelectionIndex();
		FavoriteItemVO cur = favorites.getItem(pos);
		FavoriteItemVO next = favorites.getItem(pos + 1);
		favorites.setItem(pos, next);
		favorites.setItem(pos + 1, cur);
		mTableViewer.refresh();
		updateEnabledState();
	}

	protected void handleMoveUpPressed() {
		int pos = mTableWidget.getSelectionIndex();
		FavoriteItemVO cur = favorites.getItem(pos);
		FavoriteItemVO next = favorites.getItem(pos - 1);
		favorites.setItem(pos, next);
		favorites.setItem(pos - 1, cur);
		mTableViewer.refresh();
		updateEnabledState();
	}

	private void updateEnabledState() {

		mTableWidget.setEnabled(mTableWidget.getItemCount() > 0);

		boolean enabled = (mTableWidget.getSelectionCount() > 0);
		mButtonEdit.setEnabled(enabled);
		mButtonRemove.setEnabled(enabled);
		mButtonMoveUp.setEnabled(mTableWidget.getSelectionIndex() > 0);
		mButtonMoveDown.setEnabled(mTableWidget.getSelectionIndex() > -1
				&& mTableWidget.getSelectionIndex() < mTableWidget.getItemCount() - 1);
	}

	private void handleAddPressed() {
		FavoriteItemDialog dialog = new FavoriteItemDialog(sShell);
		dialog.setFavoriteItem(new FavoriteItemVO("", ""));
		dialog.setTitle("Add");
		int reply = dialog.open();
		if (reply == Window.OK) {
			favorites.getItems().add(dialog.getFavoriteItem());
			mTableViewer.refresh();
			mTableViewer.setSelection(new StructuredSelection(dialog.getFavoriteItem()));
		}
		updateEnabledState();
		// ((NewTypesafeEnumCreationWizardPageInstances)
		// getNextPage()).setPageComplete(false);
		validatePage();
		mButtonAdd.forceFocus();
	}

	private void handleEditPressed() {
		FavoriteItemVO attribute = getSelectedFavoriteItem();
		if (attribute == null) {
			return;
		}

		FavoriteItemDialog dialog = new FavoriteItemDialog(sShell);
		dialog.setFavoriteItem(attribute);
		dialog.setTitle("Edit");
		dialog.open();
		mTableViewer.refresh();
		mTableViewer.setSelection(new StructuredSelection(dialog.getFavoriteItem()));
		updateEnabledState();
		validatePage();
	}

	private void handleRemovePressed() {
		FavoriteItemVO attribute = getSelectedFavoriteItem();
		if (attribute == null) {
			return;
		}
		favorites.getItems().remove(attribute);
		mTableViewer.refresh();
		updateEnabledState();
		validatePage();
	}

	private FavoriteItemVO getSelectedFavoriteItem() {
		IStructuredSelection selection = (IStructuredSelection) mTableViewer.getSelection();
		if (selection.isEmpty()) {
			return null;
		}

		Object element = selection.getFirstElement();
		if (element instanceof TableItem) { // workaround for bug in older
			// versions of Eclipse
			element = ((TableItem) element).getData();
		}
		FavoriteItemVO attribute = (FavoriteItemVO) element;
		return attribute;
	}

	/**
	 * @param container
	 */
	private void createButtonsPanel() {
		mButtonAdd = new Button(sShell, SWT.PUSH);
		mButtonEdit = new Button(sShell, SWT.PUSH);
		mButtonRemove = new Button(sShell, SWT.PUSH);
		mButtonMoveUp = new Button(sShell, SWT.PUSH);
		mButtonMoveDown = new Button(sShell, SWT.PUSH);

		mButtonAdd.setText("Add");
		mButtonEdit.setText("Edit");
		mButtonRemove.setText("Remove");
		mButtonMoveUp.setText("Move Up");
		mButtonMoveDown.setText("Move Down");
	}

	private void createButtons2Panel() {
		GridData tblGD = new GridData(GridData.FILL_HORIZONTAL);
		Label lab = new Label(sShell, 0);
		lab.setLayoutData(tblGD);

		mButtonOK = new Button(sShell, SWT.PUSH);
		mButtonCancel = new Button(sShell, SWT.PUSH);

		mButtonOK.setText("OK");
		mButtonCancel.setText("Cancel");
	}

	private void createTableWidget() {
		mTableWidget = new Table(sShell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		mTableWidget.setHeaderVisible(true);
		mTableWidget.setLinesVisible(true);

		int listHeight = mTableWidget.getItemHeight() * 10; // show 10 rows
		Rectangle trim = mTableWidget.computeTrim(0, 0, 0, listHeight);
		GridData tblGD = new GridData(GridData.FILL_BOTH);
		tblGD.heightHint = trim.height;
		tblGD.horizontalSpan = 8; // use both columns of grid
		mTableWidget.setLayoutData(tblGD);

		TableLayout tableLayout = new TableLayout();
		for (int element : COLUMN_WIDTHS) {
			tableLayout.addColumnData(new ColumnWeightData(1, element, true));
		}
		mTableWidget.setLayout(tableLayout);

		for (String element : COLUMN_NAMES) {
			new TableColumn(mTableWidget, SWT.NONE).setText(element);
		}
		mTableWidget.pack();

		mTableViewer = new TableViewer(mTableWidget);
		mTableViewer.setColumnProperties(COLUMN_NAMES); // must be same as
		// TableColumn text

		mTableViewer.setLabelProvider(new AttributeLabelProvider());
		mTableViewer.setContentProvider(new AttributeContentProvider());
		mTableViewer.setInput(favorites);
		mTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateEnabledState();
			}
		});
	}

	/**
	 * Validates the attributes of the <code>TypesafeEnum</code>.
	 */
	private void validatePage() {
	}

	/**
	 * Helper class that provides content for the table widget.
	 */
	protected class AttributeContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			List<FavoriteItemVO> result = new ArrayList<>();
			for (Iterator i = ((FavoritesVO) inputElement).getItems().iterator(); i.hasNext();) {
				result.add((FavoriteItemVO) i.next());
			}
			return result.toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	/**
	 * Helper class that knows how to display a table widget row.
	 */
	protected class AttributeLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof FavoriteItemVO) {
				FavoriteItemVO attribute = (FavoriteItemVO) element;

				switch (columnIndex) {
				case 0:
					return attribute.getName() == null ? "" : attribute.getName();
				case 1:
					return attribute.getPath() == null ? "" : attribute.getPath();
				}
			}
			return null;
		}
	}

}
