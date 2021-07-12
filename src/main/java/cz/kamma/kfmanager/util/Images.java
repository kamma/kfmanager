package cz.kamma.kfmanager.util;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

public class Images {

    private static Hashtable<String, Image> programIcons;

    public Images() {
    }

    public void freeAll() {
	if (programIcons != null) {
	    for (Enumeration<Image> it = programIcons.elements(); it.hasMoreElements();) {
		Image image = (Image) it.nextElement();
		image.dispose();
	    }
	    programIcons = null;
	}
    }

    Image createBitmapImage(Display display, String fileName) {
	Image result = null;
	try {
	    InputStream sourceStream = new FileInputStream(Constants.RESOURCE_DIRECTORY_RELATIVE + fileName + ".bmp");
	    ImageData source = new ImageData(sourceStream);
	    ImageData mask = source.getTransparencyMask();
	    result = new Image(display, source, mask);
	    sourceStream.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return result;
    }

    Image createIconImage(Display display, String fileName) {
	Image result = null;
	try {
	    InputStream sourceStream = new FileInputStream(Constants.RESOURCE_DIRECTORY_RELATIVE + fileName + ".gif");
	    ImageData source = new ImageData(sourceStream);
	    ImageData mask = source.getTransparencyMask();
	    result = new Image(display, source, mask);
	    sourceStream.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return result;
    }
    
    public Image getImageByResourceName(String name) {
	return (Image) programIcons.get(name);
    }

    public void loadAll(Display display) {
	programIcons = new Hashtable<String, Image>();

	programIcons.put("refresh", createIconImage(display, "refresh"));
	programIcons.put("brief", createIconImage(display, "brief"));
	programIcons.put("full", createIconImage(display, "full"));
	programIcons.put("icon_file", createIconImage(display, "icon_file"));
	programIcons.put("icon_folder", createIconImage(display, "icon_folder"));
	programIcons.put("icon_upfolder", createIconImage(display, "icon_upfolder"));

    }

    public static Image getIconForFileType(String name) {
	return (Image) programIcons.get(name);
    }

    public static Image getIconForFileType(Program program) {
	Image image = (Image) programIcons.get(program);
	if (image == null) {
	    ImageData imageData = program.getImageData();
	    if (imageData != null) {
		image = new Image(null, imageData, imageData.getTransparencyMask());
		programIcons.put(program.getName(), image);
	    } else {
		image = (Image) programIcons.get("icon_file");
	    }
	}
	return image;
    }
}
