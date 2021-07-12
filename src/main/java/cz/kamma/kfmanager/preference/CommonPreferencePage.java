package cz.kamma.kfmanager.preference;

import java.io.IOException;
import org.eclipse.jface.preference.*;

import cz.kamma.kfmanager.util.Constants;

public class CommonPreferencePage extends FieldEditorPreferencePage {

    public CommonPreferencePage() {
	super("Global settings", FieldEditorPreferencePage.GRID);

	IPreferenceStore store = PreferenceManager.getDefaultPreferenceStore();
	setPreferenceStore(store);
    }

    protected void createFieldEditors() {
	BooleanFieldEditor bSystemFiles = new BooleanFieldEditor(PreferenceManager.SETTINGS_SHOW_HIDDEN_FILES, "Show hidden files", getFieldEditorParent());
	addField(bSystemFiles);

	FontFieldEditor fontButton = new FontFieldEditor(PreferenceManager.SETTINGS_FONT_VIEWER, "Viewer font", getFieldEditorParent());
	addField(fontButton);

	FileFieldEditor fileToRun = new FileFieldEditor(PreferenceManager.SETTINGS_EDITOR, "Default editor", getFieldEditorParent());
	addField(fileToRun);

	BooleanFieldEditor bConfirmOnExit = new BooleanFieldEditor(PreferenceManager.SETTINGS_CONFIRM_ON_EXIT, "Show exit confirmation", getFieldEditorParent());
	addField(bConfirmOnExit);

    }

    protected IPreferenceStore doGetPreferenceStore() {
	PreferenceStore prefStore = new PreferenceStore(Constants.APPLICATION_PROPERTIES_PATH);
	try {
	    prefStore.load();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return prefStore;
    }
}
