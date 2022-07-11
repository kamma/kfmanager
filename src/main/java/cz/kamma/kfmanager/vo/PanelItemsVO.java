package cz.kamma.kfmanager.vo;

import java.util.List;
import java.util.Vector;

import cz.kamma.kfmanager.util.FileHelper;

public class PanelItemsVO {

	private List<FileItemVO> fileItems;

	private static int dirsCount = 0;
	private static int filesCount = 0;
	private static long filesLength = 0;
	private static int selectedDirsCount = 0;
	private static int selectedFilesCount = 0;
	private static long selectedFilesLength = 0;

	public PanelItemsVO() {
		fileItems = new Vector<>();
	}

	public PanelItemsVO(FileItemVO item) {
		fileItems = new Vector<>();
		fileItems.add(item);
	}

	public PanelItemsVO(FileItemVO[] items) {
		fileItems = new Vector<>();
		for (FileItemVO item : items)
			fileItems.add(item);
	}

	public PanelItemsVO(List<FileItemVO> fileItems) {
		this.fileItems = fileItems;
	}

	public List getFileItems() {
		return fileItems;
	}

	public void setFileItems(List<FileItemVO> fileItems) {
		this.fileItems = fileItems;
	}

	public void setFileItems(FileItemVO[] items) {
		fileItems.clear();
		for (FileItemVO item : items)
			if (item != null)
				fileItems.add(item);
	}

	public void addFileItems(FileItemVO[] items) {
		for (FileItemVO item : items)
			if (item != null)
				fileItems.add(item);
	}

	public void setFileItem(FileItemVO item) {
		fileItems.removeAll(fileItems);
		if (item != null)
			fileItems.add(item);
	}

	public void addFileItem(FileItemVO item) {
		if (item != null)
			fileItems.add(item);
	}

	private void recalculateDirAndFiles() {
		dirsCount = 0;
		filesCount = 0;
		filesLength = 0l;
		selectedDirsCount = 0;
		selectedFilesCount = 0;
		selectedFilesLength = 0l;

		for (FileItemVO fileItem : fileItems) {
			if (fileItem.isDirectory() && !fileItem.isUpDir()) {
				dirsCount++;
				if (fileItem.isSelected())
					selectedDirsCount++;
			} else if (!fileItem.isDirectory()) {
				filesCount++;
				filesLength += fileItem.getSize();
				if (fileItem.isSelected()) {
					selectedFilesCount++;
					selectedFilesLength += fileItem.getSize();
				}
			}
		}
	}

	public FileItemVO[] getSelectedItems() {
		Vector<FileItemVO> res = new Vector<>();
		for (FileItemVO fileItem : fileItems) {
			if (fileItem.isSelected())
				res.add(fileItem);
		}
		return res.toArray(new FileItemVO[0]);
	}

	public void deselectAll() {
		for (FileItemVO fileItem : fileItems) {
			fileItem.setSelected(false);
		}
	}

	public void selectInverted() {
		for (FileItemVO fileItem : fileItems) {
			if (!fileItem.isDirectory())
				fileItem.setSelected(!fileItem.isSelected());
		}
	}

	public void selectByMask(String mask) {
		for (FileItemVO fileItem : fileItems) {
			if (!fileItem.isDirectory()) {
				String fileName = fileItem.getName();
				fileItem.setSelected(FileHelper.wildcardMatch(fileName.toLowerCase(), mask.toLowerCase()) ? true
						: fileItem.isSelected());
			}
		}
	}

	public void deselectByMask(String mask) {
		for (FileItemVO fileItem : fileItems) {
			if (!fileItem.isDirectory()) {
				String fileName = fileItem.getName();
				fileItem
						.setSelected(FileHelper.wildcardMatch(fileName.toLowerCase(), mask.toLowerCase()) ? false
								: fileItem.isSelected());
			}
		}
	}

	public String getSelectedItemsStatusText() {
		recalculateDirAndFiles();
		return FileHelper.getSizeAsText(selectedFilesLength) + " / " + FileHelper.getSizeAsText(filesLength) + " in "
				+ selectedFilesCount + " / " + filesCount + " files, " + selectedDirsCount + " / " + dirsCount
				+ " dirs";
	}

}
