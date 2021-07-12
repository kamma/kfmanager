package cz.kamma.kfmanager.vo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cz.kamma.kfmanager.util.Constants;
import cz.kamma.kfmanager.util.FileHelper;

public class ZipArchivVO {

    private ZipFile zipFile = null;
    private String zipFilePath;

    public ZipArchivVO(String zipFilePath) {
	this.zipFilePath = zipFilePath;
	try {
	    zipFile = new ZipFile(zipFilePath);
	} catch (Exception e) {
	    System.out.println("Cannot create ZipArchivVO. - " + e.getMessage());
	}
    }

    public FileItemVO[] getFilesByPath(String path) {
	return getFilesByPath(path, true);
    }

    public FileItemVO[] getFilesByPath(String path, boolean withParent) {
	if (path == null || Constants.ZIP_PATH_SEPARATOR.equals(path))
	    return getRootFiles();
	if (path.startsWith(Constants.ZIP_PATH_SEPARATOR))
	    path = path.substring(Constants.ZIP_PATH_SEPARATOR.length());
	Vector<FileItemVO> res = new Vector<FileItemVO>();
	Vector<String> tmpDirNames = new Vector<String>();
	if (withParent)
	    res.add(FileHelper.getDirectoryParentInZIP(zipFilePath, path));
	for (Enumeration en = zipFile.entries(); en.hasMoreElements();) {
	    ZipEntry ze = (ZipEntry) en.nextElement();
	    if (ze.getName().startsWith(path) && !ze.getName().equals(path)) {
		String tmp_name = ze.getName().substring(path.length());
		String[] nameParts = tmp_name.split(Constants.ZIP_PATH_SEPARATOR);
		if (nameParts.length < 2 && !ze.isDirectory())
		    res.add(new FileItemVO(FileHelper.getFileNameFromZip(ze.getName()), ze.getName(), ze.isDirectory() ? Constants.FILE_TYPE_DIR : Constants.FILE_TYPE_FILE, ze.getTime(), ze.getSize(), true, zipFilePath));
		if (ze.isDirectory()) {
		    String absPath = path + nameParts[0] + Constants.ZIP_PATH_SEPARATOR;
		    if (!tmpDirNames.contains(absPath)) {
			tmpDirNames.add(absPath);
			res.add(new FileItemVO(FileHelper.getFileNameFromZip(absPath), absPath, ze.isDirectory() ? Constants.FILE_TYPE_DIR : Constants.FILE_TYPE_FILE, ze.getTime(), ze.getSize(), true, zipFilePath));
		    }
		}
	    }
	}
	return res.toArray(new FileItemVO[0]);
    }

    private FileItemVO[] getRootFiles() {
	Vector<FileItemVO> res = new Vector<FileItemVO>();
	res.add(FileHelper.getDirectoryParent(zipFilePath));
	for (Enumeration en = zipFile.entries(); en.hasMoreElements();) {
	    ZipEntry ze = (ZipEntry) en.nextElement();
	    if (!ze.getName().contains(Constants.ZIP_PATH_SEPARATOR) || (getCharCount(ze.getName(), Constants.ZIP_PATH_SEPARATOR_CHAR) == 1 && ze.getName().endsWith(Constants.ZIP_PATH_SEPARATOR)))
		res.add(new FileItemVO(FileHelper.getFileNameFromZip(ze.getName()), ze.getName(), ze.isDirectory() ? Constants.FILE_TYPE_DIR : Constants.FILE_TYPE_FILE, ze.getTime(), ze.getSize(), true, zipFilePath));
	}
	return res.toArray(new FileItemVO[0]);
    }

    public static int getCharCount(String text, char countedChar) {
	if (text == null)
	    return 0;
	int res = 0;
	for (int i = 0; i < text.length(); i++)
	    if (text.charAt(i) == countedChar)
		res++;
	return res;
    }

    public InputStream getInputStream(String fileName) {
	ZipEntry ze = zipFile.getEntry(fileName);
	try {
	    return zipFile.getInputStream(ze);
	} catch (IOException e) {
	    System.out.println("ZipArchivVO[getInputStream]: Cannot get InputStream for file: " + fileName + " (ex: " + e.getMessage() + ")");
	}
	return null;
    }

    public void close() {
	if (zipFile != null)
	    try {
		zipFile.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
    }

}
