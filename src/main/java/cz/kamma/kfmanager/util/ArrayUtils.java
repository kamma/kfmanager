package cz.kamma.kfmanager.util;

public class ArrayUtils {

	public static int getIndexInArray(Object[] array, Object obj) {
		int index = -1;
		try {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(obj)) {
					return i;
				}
			}
		} catch (Exception e) {
		}
		return index;
	}

}
