package cz.kamma.kfmanager.util;

import java.io.*;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.PatternSyntaxException;
import java.util.zip.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import cz.kamma.kfmanager.preference.PreferenceManager;
import cz.kamma.kfmanager.ui.dialog.ProgressDialog;
import cz.kamma.kfmanager.vo.FileItemVO;

public class FileHelper extends Constants {

	static String[] roots;

	public static String[] getRoots(boolean forceReload) {
		if (roots == null || forceReload)
			roots = getRootsInternal();
		return roots;
	}

	public static FileItemVO[] getFilesInDirectory(String directory) {
		File tmp = new File(directory);
		if (tmp.isDirectory()) {
			return getFileItems(tmp.listFiles());
		}
		return null;
	}

	public static String getDiskLabel(String directory) {
		String diskLabel = directory.substring(0,
				directory.indexOf(PATH_SEPARATOR) + 1);
		if (Arrays.binarySearch(roots, diskLabel) > -1)
			return diskLabel;
		else
			return null;
	}

	public static File[] getFilesInDirectoryAsFiles(String directory) {
		return (new File(directory)).listFiles();
	}

	public static FileItemVO getDirectoryParent(String directory) {
		File tmp = new File(directory);
		String par = tmp.getParent();
		if (par != null) {
			tmp = new File(par);
			return new FileItemVO(UPDIR_SYMBOL, tmp.getAbsolutePath(),
					FILE_TYPE_DIR, tmp.lastModified(), 0, "");
		}
		return null;
	}

	private static String[] getRootsInternal() {
		File[] tmp = File.listRoots();
		String[] roots = new String[tmp.length];
		for (int i = 0; i < roots.length; i++) {
			roots[i] = tmp[i].getPath();
		}
		return roots;
	}

	public static void setTableItems(Table parentTable, String[] items) {
		TableItem[] res = new TableItem[items.length];

		for (int i = 0; i < items.length; i++) {
			res[i] = new TableItem(parentTable, SWT.NONE);
			res[i].setText(0, items[i]);
		}
	}

	public static void setRootItems(Menu rootMenu, String[] items) {
		MenuItem item;
		for (int i = 0; i < items.length; i++) {
			final String drive = items[i];
			item = new MenuItem(rootMenu, SWT.CASCADE);
			item.setText(drive);
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					System.out.println("Changed to : " + drive);
				}
			});
		}
	}

	public static void setRootItemsOld(Combo rootCombo, String[] items) {
		for (int i = 0; i < items.length; i++) {
			rootCombo.add(items[i]);
		}
	}

	public static void setTableItems(Table parentTable, FileItemVO[] items) {
		parentTable.removeAll();
		TableItem[] res = new TableItem[items.length];

		for (int i = 0; i < items.length; i++) {
			res[i] = new TableItem(parentTable, SWT.NONE);
			res[i].setText(0, items[i].getName());
			res[i].setText(1, items[i].getType());
			res[i].setText(2, items[i].getSize() + "");
			res[i].setText(3, new Date(items[i].getModified()) + "");
		}
	}

	public static FileItemVO getFileItem(File file) {
		return new FileItemVO(file.getName(), file.getAbsolutePath(),
				file.isDirectory() ? FILE_TYPE_DIR : FILE_TYPE_FILE,
				file.lastModified(), file.length(), "");
	}

	public static FileItemVO[] getFileItems(File[] files) {
		FileItemVO[] res = new FileItemVO[files.length];
		for (int i = 0; i < files.length; i++) {
			if (files[i].isHidden()
					&& ConfigHelper.getInstance().getPropertyAsBoolean(
							PreferenceManager.SETTINGS_SHOW_HIDDEN_FILES, true))
				res[i] = getFileItem(files[i]);
			else if (!files[i].isHidden())
				res[i] = getFileItem(files[i]);
		}
		return res;
	}

	public static boolean createDir(String parent, String dirName) {
		if (parent != null) {
			File tmp = new File(parent);
			if (tmp.isFile())
				tmp = new File(tmp.getParent());
			tmp = new File(tmp.getAbsolutePath(), dirName);
			return tmp.mkdir();
		}
		return false;
	}

	public static boolean createFile(String parent, String fileName) {
		try {
			if (parent != null && fileName != null) {
				File tmp = new File(parent);
				if (tmp.isFile())
					tmp = new File(tmp.getParent());
				tmp = new File(tmp.getAbsolutePath(), fileName);
				return tmp.createNewFile();
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static String getNearestDirectory(String parent) {
		String res = parent;
		File tmp = new File(parent);
		if (tmp.isFile())
			res = tmp.getParent();
		return res;
	}

	public static void executeFile(Shell shell, FileItemVO file) {
		if (file == null)
			return;

		final String fileName = file.getAbsolutePath();
		if (!Program.launch(fileName)) {
			MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			dialog.setMessage(MessageFormat.format("Failed to launch file.",
					new Object[] { fileName }));
			dialog.setText(shell.getText());
			dialog.open();
		}
	}

	public static String getFileContent(FileItemVO file) {
		try {
			DataInputStream dis = new DataInputStream(file.getInputStream());
			byte[] buf = new byte[4096];
			StringBuffer strBuf = new StringBuffer();
			while (dis.available() > 0) {
				int readed = dis.read(buf);
				strBuf.append(new String(buf, 0, readed));
			}
			dis.close();
			return strBuf.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static String getFileContent(String fileName) {
		FileItemVO file = new FileItemVO(new File(fileName));
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(
					file.getAbsolutePath()));
			byte[] buf = new byte[4096];
			StringBuffer strBuf = new StringBuffer();
			while (dis.available() > 0) {
				int readed = dis.read(buf);
				strBuf.append(new String(buf, 0, readed));
			}
			dis.close();
			return strBuf.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static void saveFile(String fileName, String text) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write(text);
			fw.flush();
			fw.close();
		} catch (Exception e) {
		}
	}

	public static boolean moveFileStructure(ProgressDialog progressDialog,
			File oldFile, File newFile, boolean sub) {
		if (oldFile == null || newFile == null)
			return false;

		// ensure that newFile is not a child of oldFile or a dupe
		File searchFile = newFile;
		do {
			if (oldFile.equals(searchFile))
				return false;
			searchFile = searchFile.getParentFile();
		} while (searchFile != null);

		if (oldFile.isDirectory()) {
			/*
			 * Copy a directory
			 */
			if (progressDialog != null) {
				progressDialog.setDetailFile(oldFile.getName());
			}
			if (!sub) {
				newFile = new File(newFile.getAbsolutePath(), oldFile.getName());
			}
			if (!newFile.mkdirs())
				return false;
			File[] subFiles = oldFile.listFiles();
			if (subFiles != null) {
				if (progressDialog != null) {
					progressDialog.addWorkUnits(subFiles.length);
				}
				for (int i = 0; i < subFiles.length; i++) {
					File oldSubFile = subFiles[i];
					File newSubFile = new File(newFile, oldSubFile.getName());
					if (!moveFileStructure(progressDialog, oldSubFile,
							newSubFile, true))
						return false;
					if (progressDialog != null) {
						progressDialog.addProgress(1);
						if (progressDialog.isCancelled())
							return false;
					}
				}
			}
			oldFile.delete();
		} else {
			/*
			 * Move a file
			 */
			if (newFile.isDirectory())
				newFile = new File(newFile.getAbsolutePath(), oldFile.getName());
			oldFile.renameTo(newFile);
		}
		return true;

	}

	public static boolean copyFileStructure(ProgressDialog progressDialog,
			FileItemVO oldFile, File newFile, boolean sub) {
		if (oldFile == null || newFile == null)
			return false;

		// ensure that newFile is not a child of oldFile or a dupe
		File searchFile = newFile;
		do {
			if (oldFile.getFile().equals(searchFile))
				return false;
			searchFile = searchFile.getParentFile();
		} while (searchFile != null);

		if (oldFile.isDirectory()) {
			/*
			 * Copy a directory
			 */
			if (progressDialog != null) {
				progressDialog.setDetailFile(oldFile.getName());
			}
			if (!sub) {
				newFile = new File(newFile.getAbsolutePath(), oldFile.getName());
			}
			if (!newFile.mkdirs())
				return false;
			FileItemVO[] subFiles = oldFile.listFiles();
			if (subFiles != null) {
				if (progressDialog != null) {
					progressDialog.addWorkUnits(subFiles.length);
				}
				for (int i = 0; i < subFiles.length; i++) {
					File newSubFile = new File(newFile, subFiles[i].getName());
					if (!copyFileStructure(progressDialog, subFiles[i],
							newSubFile, true))
						return false;
					if (progressDialog != null) {
						progressDialog.addProgress(1);
						if (progressDialog.isCancelled())
							return false;
					}
				}
			}
		} else {
			/*
			 * Copy a file
			 */
			InputStream in = null;
			BufferedOutputStream out = null;
			if (newFile.isDirectory())
				newFile = new File(newFile.getAbsolutePath(), oldFile.getName());
			try {
				in = oldFile.getInputStream();
				out = new BufferedOutputStream(new FileOutputStream(newFile));

				if (progressDialog != null) {
					progressDialog.setDetailFile(oldFile.getName());
					progressDialog.addWorkUnits(in.available());
				}
				
				byte[] buffer = new byte[8192];
				int count;
				while (in.available() > 0) {
					count = in.read(buffer);
					out.write(buffer, 0, count);
					out.flush();
					if (progressDialog != null) {
						progressDialog.addProgress(count);
					}
				}
				out.close();
				in.close();
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			} finally {
				try {
					if (in != null)
						in.close();
					if (out != null)
						out.close();
				} catch (IOException e) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean deleteFileStructure(ProgressDialog progressDialog,
			File fileToDelete) {
		if (fileToDelete == null)
			return false;
		if (fileToDelete.isDirectory()) {
			/*
			 * Delete a directory
			 */
			if (progressDialog != null) {
				progressDialog.setDetailFile(fileToDelete.getName());
			}
			File[] subFiles = fileToDelete.listFiles();
			if (subFiles != null) {
				if (progressDialog != null) {
					progressDialog.addWorkUnits(subFiles.length);
				}
				for (int i = 0; i < subFiles.length; i++) {
					File oldSubFile = subFiles[i];
					if (!deleteFileStructure(progressDialog, oldSubFile))
						return false;
					if (progressDialog != null) {
						progressDialog.addProgress(1);
						if (progressDialog.isCancelled())
							return false;
					}
				}
			}
		}
		return fileToDelete.delete();
	}

	public static String getFileRoot(String path) {
		try {
			return path.substring(0, path.indexOf(PATH_SEPARATOR)
					+ PATH_SEPARATOR.length());
		} catch (Exception e) {
			System.out.println("Cannot extract FileRoot from path '" + path
					+ "'");
		}
		return PATH_SEPARATOR;
	}

	public static String getDafaultStartPath() {
		String res = System.getProperty("sun.boot.class.path");
		if (res == null || res.length() < 1)
			res = System.getProperty("java.home");
		res = getFileRoot(res);
		return res;
	}

	public static boolean regExpMatch(String filename, String regExp)
			throws PatternSyntaxException {
		if (filename == null || regExp == null) {
			return false;
		}
		boolean res = false;
		try {
			res = filename.matches(regExp);
		} catch (PatternSyntaxException e) {
			throw e;
		}
		return res;
	}

	public static boolean wildcardMatch(String filename, String wildcardMatcher) {
		if (filename == null || wildcardMatcher == null) {
			return false;
		}
		if (filename.equalsIgnoreCase("ntldr"))
			System.out.println("aaa");
		String[] wcs = splitOnTokens(wildcardMatcher);
		boolean anyChars = false;
		int textIdx = 0;
		int wcsIdx = 0;
		Stack<int[]> backtrack = new Stack<int[]>();

		// loop around a backtrack stack, to handle complex * matching
		do {
			if (backtrack.size() > 0) {
				int[] array = (int[]) backtrack.pop();
				wcsIdx = array[0];
				textIdx = array[1];
				anyChars = true;
			}

			// loop whilst tokens and text left to process
			while (wcsIdx < wcs.length) {

				if (wcs[wcsIdx].equals("?")) {
					// ? so move to next text char
					textIdx++;
					anyChars = false;

				} else if (wcs[wcsIdx].equals("*")) {
					// set any chars status
					anyChars = true;
					if (wcsIdx == wcs.length - 1) {
						textIdx = filename.length();
					}

				} else {
					// matching text token
					if (anyChars) {
						// any chars then try to locate text token
						textIdx = filename.indexOf(wcs[wcsIdx], textIdx);
						if (textIdx == -1) {
							// token not found
							break;
						}
						int repeat = filename.indexOf(wcs[wcsIdx], textIdx + 1);
						if (repeat >= 0) {
							backtrack.push(new int[] { wcsIdx, repeat });
						}
					} else {
						// matching from current position
						if (!filename.startsWith(wcs[wcsIdx], textIdx)) {
							// couldnt match token
							break;
						}
					}

					// matched text token, move text index to end of matched
					// token
					textIdx += wcs[wcsIdx].length();
					anyChars = false;
				}

				wcsIdx++;
			}

			// full match
			if (wcsIdx == wcs.length && textIdx == filename.length()) {
				return true;
			}

		} while (backtrack.size() > 0);

		return false;
	}

	static String[] splitOnTokens(String text) {
		// used by wildcardMatch
		// package level so a unit test may run on this

		if (text.indexOf("?") == -1 && text.indexOf("*") == -1) {
			return new String[] { text };
		}

		char[] array = text.toCharArray();
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '?' || array[i] == '*') {
				if (buffer.length() != 0) {
					list.add(buffer.toString());
					buffer.setLength(0);
				}
				if (array[i] == '?') {
					list.add("?");
				} else if (list.size() == 0
						|| (i > 0 && list.get(list.size() - 1).equals("*") == false)) {
					list.add("*");
				}
			} else {
				buffer.append(array[i]);
			}
		}
		if (buffer.length() != 0) {
			list.add(buffer.toString());
		}
		return (String[]) list.toArray(new String[0]);
	}

	public static String getFileNameFromString(String name) {
		if (name.indexOf(PATH_SEPARATOR) > -1)
			return name.substring(name.lastIndexOf(PATH_SEPARATOR));
		return name;
	}

	public static String getFileNameFromZip(String name) {
		if (name.endsWith(ZIP_PATH_SEPARATOR))
			name = name.substring(0,
					name.length() - ZIP_PATH_SEPARATOR.length());
		if (name.indexOf(ZIP_PATH_SEPARATOR) > -ZIP_PATH_SEPARATOR.length())
			return name.substring(name.lastIndexOf(ZIP_PATH_SEPARATOR)
					+ ZIP_PATH_SEPARATOR.length());
		return name;
	}

	public static FileItemVO getDirectoryParentInZIP(String zipFile, String path) {
		File tmp = new File(path);
		String par = tmp.getParent();
		if (par != null) {
			if (!par.endsWith(ZIP_PATH_SEPARATOR))
				par = par + ZIP_PATH_SEPARATOR;
			par.replace(BACKSLASH_CHAR, ZIP_PATH_SEPARATOR_CHAR);
			return new FileItemVO(UPDIR_SYMBOL, par, FILE_TYPE_DIR, 0, 0, true,
					zipFile);
		}
		return new FileItemVO(UPDIR_SYMBOL, ZIP_PATH_SEPARATOR, FILE_TYPE_DIR,
				0, 0, true, zipFile);
	}

	public static String getZipPathFromLabel(String label) {
		if (label != null) {
			String[] parts = label.split(Constants.ZIP_FILE_AND_PATH_SEPARATOR);
			if (parts.length == 2)
				return parts[1];
		}
		return label;
	}

	public static boolean isPathExists(String pathOrFile) {
		File tmp = new File(pathOrFile);
		return tmp.exists();
	}

	public static boolean packFileStructure(ProgressDialog progressDialog,
			FileItemVO oldFile, ZipOutputStream zipFile, String parentDir,
			boolean sub) {
		if (oldFile == null || zipFile == null)
			return false;

		if (oldFile.isDirectory()) {
			/*
			 * Create a directory in zip file
			 */
			if (progressDialog != null) {
				progressDialog.setDetailFile(oldFile.getName());
			}
			String entryName = oldFile.getAbsolutePath().substring(
					parentDir.length());
			entryName = entryName.replace(BACKSLASH_CHAR,
					ZIP_PATH_SEPARATOR_CHAR);
			entryName = entryName + ZIP_PATH_SEPARATOR;
			if (entryName.startsWith(ZIP_PATH_SEPARATOR))
				entryName = entryName.substring(ZIP_FILE_AND_PATH_SEPARATOR
						.length());

			ZipEntry ze = new ZipEntry(entryName);
			try {
				zipFile.putNextEntry(ze);
				zipFile.closeEntry();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileItemVO[] subFiles = oldFile.listFiles();
			if (subFiles != null) {
				if (progressDialog != null) {
					progressDialog.addWorkUnits(subFiles.length);
				}
				for (int i = 0; i < subFiles.length; i++) {
					if (!packFileStructure(progressDialog, subFiles[i],
							zipFile, parentDir, true))
						return false;
					if (progressDialog != null) {
						progressDialog.addProgress(1);
						if (progressDialog.isCancelled())
							return false;
					}
				}
			}
		} else {
			/*
			 * Copy a file
			 */
			String entryName = oldFile.getAbsolutePath().substring(
					parentDir.length());
			entryName = entryName.replace(BACKSLASH_CHAR,
					ZIP_PATH_SEPARATOR_CHAR);
			if (entryName.startsWith(ZIP_PATH_SEPARATOR))
				entryName = entryName.substring(ZIP_FILE_AND_PATH_SEPARATOR
						.length());

			ZipEntry ze = new ZipEntry(entryName);

			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(oldFile.getInputStream());

				byte[] buffer = new byte[8192];
				int count;

				zipFile.putNextEntry(ze);
				while (in.available() > 0) {
					count = in.read(buffer);
					zipFile.write(buffer, 0, count);
				}
				zipFile.closeEntry();
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	public static String getSizeAsText(long size) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		if (size > 1024) {
			return nf.format(size / 1024) + " KB";
		} else {
			return nf.format(size) + " B";
		}
	}

	public static boolean isZipFile(String name) {
		if (name == null || name.indexOf(".") < 0)
			return false;
		String ext = name.substring(name.lastIndexOf('.') + 1);
		for (int i = 0; i < ZIP_ARCHIVE_EXTENSIONS.length; i++) {
			if (ext.equalsIgnoreCase(ZIP_ARCHIVE_EXTENSIONS[i])) {
				try {
					new ZipFile(name);
					return true;
				} catch (Exception e) {
				}
			}
		}
		return false;
	}

}
