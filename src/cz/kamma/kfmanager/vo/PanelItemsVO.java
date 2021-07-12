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
	fileItems = new Vector<FileItemVO>();
    }

    public PanelItemsVO(FileItemVO item) {
	fileItems = new Vector<FileItemVO>();
	fileItems.add(item);
    }

    public PanelItemsVO(FileItemVO[] items) {
	fileItems = new Vector<FileItemVO>();
	for (int i = 0; i < items.length; i++)
	    fileItems.add(items[i]);
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
	for (int i = 0; i < items.length; i++)
	    if (items[i] != null)
		fileItems.add(items[i]);
    }

    public void addFileItems(FileItemVO[] items) {
	for (int i = 0; i < items.length; i++)
	    if (items[i] != null)
		fileItems.add(items[i]);
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

	for (int i = 0; i < fileItems.size(); i++) {
	    if (fileItems.get(i).isDirectory() && !fileItems.get(i).isUpDir()) {
		dirsCount++;
		if (fileItems.get(i).isSelected())
		    selectedDirsCount++;
	    } else if (!fileItems.get(i).isDirectory()) {
		filesCount++;
		filesLength += fileItems.get(i).getSize();
		if (fileItems.get(i).isSelected()) {
		    selectedFilesCount++;
		    selectedFilesLength += fileItems.get(i).getSize();
		}
	    }
	}
    }

    public FileItemVO[] getSelectedItems() {
	Vector<FileItemVO> res = new Vector<FileItemVO>();
	for (int i = 0; i < fileItems.size(); i++) {
	    if (fileItems.get(i).isSelected())
		res.add(fileItems.get(i));
	}
	return res.toArray(new FileItemVO[0]);
    }

    public void deselectAll() {
	for (int i = 0; i < fileItems.size(); i++) {
	    fileItems.get(i).setSelected(false);
	}
    }

    public void selectInverted() {
	for (int i = 0; i < fileItems.size(); i++) {
	    if (!fileItems.get(i).isDirectory())
		fileItems.get(i).setSelected(!fileItems.get(i).isSelected());
	}
    }

    public void selectByMask(String mask) {
	for (int i = 0; i < fileItems.size(); i++) {
	    if (!fileItems.get(i).isDirectory()) {
		String fileName = fileItems.get(i).getName();
		fileItems.get(i).setSelected(FileHelper.wildcardMatch(fileName.toLowerCase(), mask.toLowerCase()) ? true : fileItems.get(i).isSelected());
	    }
	}
    }

    public void deselectByMask(String mask) {
	for (int i = 0; i < fileItems.size(); i++) {
	    if (!fileItems.get(i).isDirectory()) {
		String fileName = fileItems.get(i).getName();
		fileItems.get(i).setSelected(FileHelper.wildcardMatch(fileName.toLowerCase(), mask.toLowerCase()) ? false : fileItems.get(i).isSelected());
	    }
	}
    }

    public String getSelectedItemsStatusText() {
	recalculateDirAndFiles();
	return FileHelper.getSizeAsText(selectedFilesLength) + " / " + FileHelper.getSizeAsText(filesLength) + " in " + selectedFilesCount + " / " + filesCount + " files, " + selectedDirsCount + " / " + dirsCount + " dirs";
    }

}
