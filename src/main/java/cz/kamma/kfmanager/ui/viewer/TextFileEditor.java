package cz.kamma.kfmanager.ui.viewer;

import java.util.Vector;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import cz.kamma.kfmanager.preference.PreferenceManager;
import cz.kamma.kfmanager.util.Constants;
import cz.kamma.kfmanager.util.FileHelper;
import cz.kamma.kfmanager.vo.FileItemVO;

/**
 */
public class TextFileEditor {
	Shell shell;
	StyledText text;

	Vector<StyleRange> cachedStyles = new Vector<>();
	Font font = null;
	private FileItemVO file;
	boolean fileLoaded = false;
	boolean isEditor = false;
	MenuItem saveMenuItem, saveAsMenuItem;

	public TextFileEditor(Shell parent, FileItemVO file, boolean isEditor) {
		this.file = file;
		this.isEditor = isEditor;
		Display display = parent.getDisplay();
		Shell sShell = open(new Shell(parent));
		while (!sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	Menu createEditMenu() {
		Menu bar = shell.getMenuBar();
		Menu menu = new Menu(bar);

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Cut");
		item.setAccelerator(SWT.MOD1 + 'X');
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleCutCopy();
				text.cut();
			}
		});
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Copy");
		item.setAccelerator(SWT.MOD1 + 'C');
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleCutCopy();
				text.copy();
			}
		});
		new MenuItem(menu, SWT.SEPARATOR);
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Font");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setFont();
			}
		});
		return menu;
	}

	Menu createFileMenu() {
		Menu bar = shell.getMenuBar();
		Menu menu = new Menu(bar);

		if (isEditor) {
			saveMenuItem = new MenuItem(menu, SWT.PUSH);
			saveMenuItem.setText("Save");
			saveMenuItem.setEnabled(false);
			saveMenuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					saveFileAs(file.getAbsolutePath());
				}
			});

			saveAsMenuItem = new MenuItem(menu, SWT.PUSH);
			saveAsMenuItem.setText("Save As");
			saveAsMenuItem.setEnabled(false);
			saveAsMenuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					FileDialog fileName = new FileDialog(shell, SWT.SAVE);
					fileName.setFileName(file.getAbsolutePath());
					saveFileAs(fileName.open());
				}
			});
			new MenuItem(menu, SWT.SEPARATOR);
		}

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Exit");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});

		return menu;
	}

	protected void saveFileAs(String fileName) {
		if (fileName == null || fileName.length() < 1)
			return;

		FileHelper.saveFile(fileName, text.getText());

		saveAsMenuItem.setEnabled(false);
		saveMenuItem.setEnabled(false);
	}

	/*
	 * Clear all style data for the selected text.
	 */
	void clear() {
		Point sel = text.getSelectionRange();
		if ((sel != null) && (sel.y != 0)) {
			StyleRange style;
			style = new StyleRange(sel.x, sel.y, null, null, SWT.NORMAL);
			text.setStyleRange(style);
		}
		text.setSelectionRange(sel.x + sel.y, 0);
	}

	/*
	 * Set the foreground color for the selected text.
	 */
	void fgColor(Color fg) {
		Point sel = text.getSelectionRange();
		if ((sel == null) || (sel.y == 0))
			return;
		StyleRange style, range;
		for (int i = sel.x; i < sel.x + sel.y; i++) {
			range = text.getStyleRangeAtOffset(i);
			if (range != null) {
				style = (StyleRange) range.clone();
				style.start = i;
				style.length = 1;
				style.foreground = fg;
			} else {
				style = new StyleRange(i, 1, fg, null, SWT.NORMAL);
			}
			text.setStyleRange(style);
		}
		text.setSelectionRange(sel.x + sel.y, 0);
	}

	void createMenuBar() {
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);

		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
		fileItem.setText("File");
		fileItem.setMenu(createFileMenu());

		MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
		editItem.setText("Edit");
		editItem.setMenu(createEditMenu());
	}

	void createShell(Shell sShell) {
		shell = new Shell(sShell.getDisplay());
		String tmpType = isEditor ? "Edit" : "View";
		shell.setText(Constants.APPLICATION_TITLE + " - " + tmpType + " [" + file.getName() + "]");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		shell.setLayout(layout);
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (isEditor && saveAsMenuItem.isEnabled()) {
					MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					dialog.setText(Constants.APPLICATION_NAME);
					dialog.setMessage("Save changes to " + file.getName() + " ?");
					int res = dialog.open();
					if (res == SWT.YES) {
						saveFileAs(file.getAbsolutePath());
					}
				}
				if (font != null)
					font.dispose();
			}
		});
		if (!isEditor) {
			shell.getDisplay().addFilter(SWT.KeyDown, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (event.keyCode == SWT.ESC) {
						shell.dispose();
					} else if (event.keyCode == SWT.CTRL + 'S') {
						saveFileAs(file.getAbsolutePath());
					}
				}
			});
		}
	}

	void createStyledText() {
		if (isEditor)
			text = new StyledText(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		else
			text = new StyledText(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		text.setText(FileHelper.getFileContent(file));
		GridData spec = new GridData();
		spec.horizontalAlignment = GridData.FILL;
		spec.grabExcessHorizontalSpace = true;
		spec.verticalAlignment = GridData.FILL;
		spec.grabExcessVerticalSpace = true;
		text.setLayoutData(spec);
		text.addExtendedModifyListener(new ExtendedModifyListener() {
			@Override
			public void modifyText(ExtendedModifyEvent e) {
				handleExtendedModify(e);
				saveMenuItem.setEnabled(true);
				saveAsMenuItem.setEnabled(true);
			}
		});
	}

	/*
	 * Cache the style information for text that has been cut or copied.
	 */
	void handleCutCopy() {
		// Save the cut/copied style info so that during paste we will maintain
		// the style information. Cut/copied text is put in the clipboard in
		// RTF format, but is not pasted in RTF format. The other way to
		// handle the pasting of styles would be to access the Clipboard
		// directly
		// and
		// parse the RTF text.
		cachedStyles = new Vector<>();
		Point sel = text.getSelectionRange();
		int startX = sel.x;
		for (int i = sel.x; i <= sel.x + sel.y - 1; i++) {
			StyleRange style = text.getStyleRangeAtOffset(i);
			if (style != null) {
				style.start = style.start - startX;
				if (!cachedStyles.isEmpty()) {
					StyleRange lastStyle = cachedStyles.lastElement();
					if (lastStyle.similarTo(style) && lastStyle.start + lastStyle.length == style.start) {
						lastStyle.length++;
					} else {
						cachedStyles.addElement(style);
					}
				} else {
					cachedStyles.addElement(style);
				}
			}
		}
	}

	void handleExtendedModify(ExtendedModifyEvent event) {
		if (event.length == 0)
			return;
		StyleRange style;
		if (event.length == 1 || text.getTextRange(event.start, event.length).equals(text.getLineDelimiter())) {
			// Have the new text take on the style of the text to its right
			// (during
			// typing) if no style information is active.
			int caretOffset = text.getCaretOffset();
			style = null;
			if (caretOffset < text.getCharCount())
				style = text.getStyleRangeAtOffset(caretOffset);
			if (style != null) {
				style = (StyleRange) style.clone();
				style.start = event.start;
				style.length = event.length;
			} else {
				style = new StyleRange(event.start, event.length, null, null, SWT.NORMAL);
			}
			if (!style.isUnstyled())
				text.setStyleRange(style);
		} else {
			// paste occurring, have text take on the styles it had when it was
			// cut/copied
			for (int i = 0; i < cachedStyles.size(); i++) {
				style = cachedStyles.elementAt(i);
				StyleRange newStyle = (StyleRange) style.clone();
				newStyle.start = style.start + event.start;
				text.setStyleRange(newStyle);
			}
		}
	}

	public Shell open(Shell sShell) {
		createShell(sShell);
		createMenuBar();
		createStyledText();
		setFontDefault();
		shell.setSize(800, 600);
		shell.open();
		return shell;
	}

	void setFontDefault() {
		text.setFont(new Font(shell.getDisplay(), PreferenceConverter
				.getFontData(PreferenceManager.getDefaultPreferenceStore(), PreferenceManager.SETTINGS_FONT_VIEWER)));
	}

	void setFont() {
		FontDialog fontDialog = new FontDialog(shell);
		fontDialog.setFontList((text.getFont()).getFontData());
		FontData fontData = fontDialog.open();
		if (fontData != null) {
			if (font != null) {
				font.dispose();
			}
			font = new Font(shell.getDisplay(), fontData);
			text.setFont(font);
			PreferenceConverter.setValue(PreferenceManager.getDefaultPreferenceStore(),
					PreferenceManager.SETTINGS_FONT_VIEWER, fontData);
		}
	}

}
