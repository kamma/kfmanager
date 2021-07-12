package cz.kamma.kfmanager.preference;

import java.io.IOException;
import org.eclipse.jface.preference.*;

import cz.kamma.kfmanager.util.Constants;

public class PanelPreferencePage extends FieldEditorPreferencePage {

    String preferencesPerfix;

    public PanelPreferencePage(String panelName, String preferencesPerfix) {
	super(panelName, FieldEditorPreferencePage.GRID);

	this.preferencesPerfix = preferencesPerfix;

	IPreferenceStore store = PreferenceManager.getDefaultPreferenceStore();
	setPreferenceStore(store);
    }

    protected void createFieldEditors() {
	FieldEditor editor;

	editor = new FontFieldEditor(preferencesPerfix + PreferenceManager.SETTINGS_FONT, "File panel font", getFieldEditorParent());
	addField(editor);

	editor = new RadioGroupFieldEditor(preferencesPerfix + PreferenceManager.SETTINGS_PANELS_SHOW_TYPE, "Panel view type", 1, new String[][] { { "Full", "Full" }, { "Brief", "Brief" } }, getFieldEditorParent());
	addField(editor);

	editor = new BooleanFieldEditor(preferencesPerfix + PreferenceManager.SETTINGS_SHOW_TABLE_HEADER, "Show panel header", getFieldEditorParent());
	addField(editor);

	editor = new BooleanFieldEditor(preferencesPerfix + PreferenceManager.SETTINGS_SHOW_TABLE_LINES, "Show panel lines", getFieldEditorParent());
	addField(editor);

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
