package cz.kamma.kfmanager.util;

import java.io.*;
import java.util.Properties;

public class ConfigHelper {
    private static ConfigHelper instance;

    private String fileName;

    private Properties props;

    private ConfigHelper(String fileName) {
	this.fileName = fileName;
	loadProperties();
    }

    public void loadProperties() {
	props = new Properties();
	try {
	    props.load(new FileInputStream(fileName));
	} catch (Exception e) {
	    System.out.println("Error while loading properties: " + e.getMessage());
	}
    }

    public static ConfigHelper getInstance() {
	if (instance == null || !instance.fileName.equalsIgnoreCase(getPreferenceFile()))
	    ;
	instance = new ConfigHelper(getPreferenceFile());
	return instance;
    }

    public static ConfigHelper getInstance(String fileName) {
	if (instance == null || !instance.fileName.equalsIgnoreCase(fileName))
	    ;
	instance = new ConfigHelper(fileName);
	return instance;
    }

    public Properties getProperties() {
	return props;
    }

    public void setProperties(Properties props) {
	this.props = props;
    }

    public void storeProperties() {
	try {
	    props.store(new FileOutputStream(fileName), null);
	} catch (Exception e) {
	    System.out.println("Error while saving properties: " + e.getMessage());
	}
    }

    public void setProperty(String key, String value) {
	props.setProperty(key, value);
	storeProperties();
    }

    public void setProperty(String key, int value) {
	setProperty(key, Integer.toString(value));
    }

    public String getProperty(String key, String defaultValue) {
	loadProperties();
	String res = props.getProperty(key);
	if (res == null || res.length() == 0) {
	    return defaultValue;
	}
	return res;
    }

    public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
	String res = getProperty(key, null);
	if (res == null || res.length() == 0) {
	    return defaultValue;
	}
	return "TRUE".equalsIgnoreCase(res);
    }

    public int getProperty(String key, int defaultValue) {
	try {
	    return Integer.parseInt(getProperty(key, defaultValue + ""));
	} catch (Exception e) {
	    System.out.println("Cannot parse value '" + props.getProperty(key) + "' to int.");
	}
	return defaultValue;
    }

    public static String getPreferenceFile() {
	return Constants.APPLICATION_PROPERTIES_PATH;
    }

}
