package cz.kamma.kfmanager.util;

import java.text.DateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class Constants {

	public static final String APPLICATION_NAME = "kAmMa\'s file manager";
	public static final String APPLICATION_VERSION = "0.1.0";

	public static final boolean WINDOWS_PLATFORM = System.getProperty("os.name").startsWith("Windows");

	public static final String APPLICATION_TITLE = APPLICATION_NAME + " - v" + APPLICATION_VERSION;

	public static String[] COMMANDS_FOR_MANUAL_PROCESSION = { "cd", "md", "chdir", "mkdir", "rd", "rmdir" };

	public static String[] ZIP_ARCHIVE_EXTENSIONS = { "zip", "jar", "ear" };

	public static final String[] COLUMN_NAMES = new String[] { "Name", "Size", "Type", "Date" };
	public static final int[] COLUMN_WIDTHS = new int[] { 150, 40, 100, 110 };

	public static final Color COLOR_BLUE = new Color(null, 51, 94, 168);
	public static final Color COLOR_LIGHTBLUE = new Color(null, 111, 161, 217);
	public static final Color COLOR_WHITE = new Color(null, 255, 255, 255);
	public static final Color COLOR_RED = new Color(null, 255, 20, 30);

	public static final Font FONT_BOLD = new Font(null, new FontData("Arial", 8, SWT.BOLD));

	public static final String PATH_SEPARATOR = java.io.File.separator;

	public static final String RESOURCE_DIRECTORY_RELATIVE = "res" + PATH_SEPARATOR;
	public static final String RESOURCE_ABOUT_FILE = "about.txt";
	public static final String RESOURCE_SHORTCUTS_FILE = "shortcuts.txt";

	public static final char BACKSLASH_CHAR = '\\';

	public static final DateFormat FILE_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
			DateFormat.MEDIUM);

	public static final String[] COMMAND_BUTTON_LABELS = new String[] { "F3 View", "F4 Edit", "F5 Copy", "F6 Move",
			"F7 New Folder", "F8 Delete", "Alt-F4 Exit" };
	public static final int COMMAND_BUTTON_VIEW = 1;
	public static final int COMMAND_BUTTON_EDIT = 2;
	public static final int COMMAND_BUTTON_COPY = 3;
	public static final int COMMAND_BUTTON_MOVE = 4;
	public static final int COMMAND_BUTTON_NEW_FOLDER = 5;
	public static final int COMMAND_BUTTON_DELETE = 6;
	public static final int COMMAND_BUTTON_EXIT = 7;

	public static final String FILE_TYPE_DIR = "D";
	public static final String FILE_TYPE_FILE = "F";

	public static final String UPDIR_SYMBOL = "..";

	public static final String APPLICATION_FAVORITES_PATH = "favorites.cfg";
	public static final String APPLICATION_PROPERTIES_PATH = "kfm.cfg";

	public static final String APPLICATION_PROPERTIES_LEFT_PATH = "LEFT_PANEL_PATH";
	public static final String APPLICATION_PROPERTIES_RIGHT_PATH = "RIGHT_PANEL_PATH";
	public static final String APPLICATION_PROPERTIES_POS_X = "START_POS_X";
	public static final String APPLICATION_PROPERTIES_POS_Y = "START_POS_Y";
	public static final String APPLICATION_PROPERTIES_WIDTH = "START_WIDTH";
	public static final String APPLICATION_PROPERTIES_HEIGHT = "START_HEIGHT";
	public static final String APPLICATION_PROPERTIES_SHOW_EXIT_DIALOG = "SHOW_EXIT_DIALOG";

	public static final char ZIP_PATH_SEPARATOR_CHAR = '/';
	public static final String ZIP_PATH_SEPARATOR = String.valueOf(ZIP_PATH_SEPARATOR_CHAR);
	public static final String ZIP_FILE_AND_PATH_SEPARATOR = ">";

}
