package cz.kamma.kfmanager.vo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipFile;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;

import cz.kamma.kfmanager.util.Constants;
import cz.kamma.kfmanager.util.FileHelper;
import cz.kamma.kfmanager.util.Images;

public class FileItemVO extends Constants {

	private String name;
	private String absolutePath;
	private String type;
	private long modified;
	private long size;
	private String description;
	private boolean selected;
	private boolean isInZip = false;
	private String zipFile;

	public FileItemVO(String name, String absolutePath, String type, long modified, long size, String description) {
		this.name = name;
		this.absolutePath = absolutePath;
		this.type = type;
		this.modified = modified;
		this.size = size;
		this.description = description;
	}

	public FileItemVO(String name, String absolutePath, String type) {
		this.name = name;
		this.absolutePath = absolutePath;
		this.type = type;
	}

	public FileItemVO(String name, String absolutePath, String type, long modified, long size, boolean isInZip,
			String zipFile) {
		this.name = name;
		this.absolutePath = absolutePath;
		this.type = type;
		this.modified = modified;
		this.size = size;
		this.isInZip = isInZip;
		this.zipFile = zipFile;
	}

	public FileItemVO(File file) {
		this.name = file.getName();
		this.absolutePath = file.getAbsolutePath();
		this.type = file.isDirectory() ? FILE_TYPE_DIR : FILE_TYPE_FILE;
		this.modified = file.lastModified();
		this.size = file.length();
	}

	public FileItemVO(File file, boolean isInZip) {
		this(file);
		this.isInZip = isInZip;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String fullPath) {
		this.absolutePath = fullPath;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCurrentDir() {
		return FileHelper.getNearestDirectory(getAbsolutePath());
	}

	public String getParentDir() {
		return new File(getCurrentDir()).getParent();
	}

	public String getStatusLine() {
		String res = "";
		if (isDirectory()) {
			res = "[" + getName() + "]\t<DIR>\t" + Constants.FILE_DATE_FORMAT.format(new Date(getModified()));
		} else {
			res = getName() + "\t" + FileHelper.getSizeAsText(getSize()) + "\t"
					+ Constants.FILE_DATE_FORMAT.format(new Date(getModified()));
		}
		return res;
	}

	public boolean isDirectory() {
		return FILE_TYPE_DIR.equals(getType());
	}

	public boolean isUpDir() {
		return UPDIR_SYMBOL.equals(getName());
	}

	public String getTypeByExt() {
		String typeString;
		if (isDirectory()) {
			typeString = ("Folder");
		} else {
			int dot = getName().lastIndexOf('.');
			if (dot != -1) {
				String extension = getName().substring(dot);
				Program program = Program.findProgram(extension);
				if (program != null) {
					typeString = program.getName();
				} else {
					typeString = MessageFormat.format("Unknown", new Object[] { extension.toUpperCase() });
				}
			} else {
				typeString = "None";
			}
		}
		return typeString;
	}

	public Image getIconFileByExt() {
		Image iconImage;

		if (UPDIR_SYMBOL.equals(getName())) {
			iconImage = Images.getIconForFileType("icon_upfolder");
		} else if (isDirectory()) {
			iconImage = Images.getIconForFileType("icon_folder");
		} else {
			int dot = getName().lastIndexOf('.');
			if (dot != -1) {
				String extension = getName().substring(dot);
				Program program = Program.findProgram(extension);
				if (program != null) {
					iconImage = Images.getIconForFileType(program);
				} else {
					iconImage = Images.getIconForFileType("icon_file");
				}
			} else {
				iconImage = Images.getIconForFileType("icon_file");
			}
		}
		return iconImage;
	}

	public String getExtension() {
		String extension = "";
		int dot = getName().lastIndexOf('.');
		if (dot != -1) {
			extension = getName().substring(dot);
		}
		return extension;
	}

	@Override
	public String toString() {
		return getName();
	}

	public File getFile() {
		return new File(getAbsolutePath());
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void rename(String newName) {
		File oldFile = getFile();
		File newFile = new File(oldFile.getParent(), newName);
		if (oldFile.renameTo(newFile)) {
			setName(newName);
			setAbsolutePath(newFile.getAbsolutePath());
		}
	}

	public boolean isZipFile() {
		try {
			ZipFile zf = new ZipFile(getAbsolutePath());
			return zf.entries().hasMoreElements();
		} catch (IOException e) {
			System.out.println("Error in zip file.");
		}
		return false;
	}

	public boolean isInZip() {
		return isInZip;
	}

	public void setInZip(boolean isInZip) {
		this.isInZip = isInZip;
	}

	public Enumeration getZipFileEntries() {
		try {
			ZipFile zf = new ZipFile(getAbsolutePath());
			return zf.entries();
		} catch (IOException e) {
			System.out.println("Error in zip file.");
		}
		return null;
	}

	public ZipArchivVO getZipArchivVO() {
		if (zipFile != null)
			return new ZipArchivVO(zipFile);
		return new ZipArchivVO(getAbsolutePath());
	}

	public InputStream getInputStream() {
		if (isDirectory())
			return null;
		InputStream is = null;
		try {
			if (!isInZip()) {
				is = new FileInputStream(getFile());
			} else if (isInZip) {
				ZipArchivVO zipFile = new ZipArchivVO(getZipFile());
				is = zipFile.getInputStream(getAbsolutePath());
			}
		} catch (Exception e) {
			System.out.println("FileItemVO[getInputStream]: Cannot get InputStream for file: " + getAbsolutePath()
					+ " (ex: " + e.getMessage() + ")");
		}
		return is;
	}

	public String getZipFile() {
		return zipFile;
	}

	public void setZipFile(String zipFile) {
		this.zipFile = zipFile;
	}

	public FileItemVO[] listFiles() {
		if (!isDirectory())
			return null;
		FileItemVO[] res = null;

		if (!isInZip()) {
			File[] files = getFile().listFiles();
			res = new FileItemVO[files.length];
			for (int i = 0; i < files.length; i++)
				res[i] = new FileItemVO(files[i]);
		} else if (isInZip()) {
			ZipArchivVO zip = getZipArchivVO();
			res = zip.getFilesByPath(getAbsolutePath(), false);
			zip.close();
		}

		return res;
	}

	public String getLabelTextForZipFile() {
		return getZipFile() + ZIP_FILE_AND_PATH_SEPARATOR + getAbsolutePath();
	}

	public String getNameWithoutExt() {
		if (isDirectory() || (getName().lastIndexOf(".") < 0))
			return getName();
		return getName().substring(0, getName().lastIndexOf("."));
	}

}
