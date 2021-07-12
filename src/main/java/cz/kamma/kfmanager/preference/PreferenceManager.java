package cz.kamma.kfmanager.preference;

import java.io.IOException;

import org.eclipse.jface.preference.*;

import cz.kamma.kfmanager.util.Constants;

/**
 * Created on Oct 16, 2004
 * 
 * @author Bertalan Lacza
 */
public class PreferenceManager extends org.eclipse.jface.preference.PreferenceManager {

    private static PreferenceStore preferenceStore = null;

    public final static String SETTINGS_PREFIX_LEFT = "LEFT_";
    public final static String SETTINGS_PREFIX_RIGHT = "RIGHT_";

    public final static String SETTINGS_FONT = "FONT";
    public final static String SETTINGS_FONT_VIEWER = "FONT_PREFERENCE_VIEWER";
    public final static String SETTINGS_SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES";
    public final static String SETTINGS_SHOW_FILE_EXTENSION = "SHOW_FILE_EXTENSION";
    public final static String SETTINGS_EDITOR = "EDITOR";
    public final static String SETTINGS_CONFIRM_ON_EXIT = "CONFIRM_ON_EXIT";

    public final static String SETTINGS_PANELS_SHOW_TYPE = "PANELS_SHOW_TYPE";
    public final static String SETTINGS_SHOW_TABLE_HEADER = "SHOW_TABLE_HEADER";
    public final static String SETTINGS_SHOW_TABLE_LINES = "SHOW_TABLE_LINES";

    /**
     * Contructor.
     * 
     */
    public PreferenceManager() {
	super('/');
	this.createManager();
    }

    /**
     * Creates content.
     * 
     */
    private void createManager() {
	PreferenceNode pNode;

	pNode = new PreferenceNode("1", new CommonPreferencePage());
	addToRoot(pNode);

	pNode = new PreferenceNode("2.1", new PanelPreferencePage("Left panel settings", SETTINGS_PREFIX_LEFT));
	addToRoot(pNode);

	pNode = new PreferenceNode("2.2", new PanelPreferencePage("Right panel settings", SETTINGS_PREFIX_RIGHT));
	addToRoot(pNode);

    }

    /**
     * Returns the default preference store.
     * 
     * @return the default preference store
     */
    public static IPreferenceStore getDefaultPreferenceStore() {
	/* Create the preference store lazily */
	if (preferenceStore == null) {
	    preferenceStore = new PreferenceStore(Constants.APPLICATION_PROPERTIES_PATH);
	    try {
		preferenceStore.load();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return preferenceStore;
    }

    /**
     * 
     * 
     */
    public void refresh() {
	removeAll();
	this.createManager();
    }
}
