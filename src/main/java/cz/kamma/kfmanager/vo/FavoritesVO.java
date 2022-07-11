package cz.kamma.kfmanager.vo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import cz.kamma.kfmanager.util.ConfigHelper;
import cz.kamma.kfmanager.util.Constants;

public class FavoritesVO implements Serializable {

	private static final long serialVersionUID = 3220454249341064804L;

	private Vector<FavoriteItemVO> items;

	public FavoritesVO() {
		loadFavorites();
	}

	public Vector<FavoriteItemVO> getItems() {
		return items;
	}

	public void setItems(Vector<FavoriteItemVO> items) {
		this.items = items;
	}

	public void loadFavorites() {
		loadFavoritesFromObjectFile();
	}

	public void loadFavoritesFromPropertyFile() {
		items = new Vector<>();
		try {
			ConfigHelper ch = ConfigHelper.getInstance(Constants.APPLICATION_FAVORITES_PATH);
			Properties favs = ch.getProperties();
			for (Enumeration<Object> en = favs.keys(); en.hasMoreElements();) {
				String name = (String) en.nextElement();
				items.add(new FavoriteItemVO(name, favs.getProperty(name)));
			}
		} catch (Exception e) {
		}
	}

	public void loadFavoritesFromObjectFile() {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(Constants.APPLICATION_FAVORITES_PATH));
			items = (Vector<FavoriteItemVO>) in.readObject();
			in.close();
		} catch (Exception e) {
			items = new Vector<>();
		}
	}

	public void storeFavorites() {
		storeFavoritesToObjectFile();
	}

	public void storeFavoritesToPropertyFile() {
		try {
			Properties favs = new Properties();
			for (FavoriteItemVO item : items) {
				favs.put(item.getName(), item.getPath());
			}
			ConfigHelper ch = ConfigHelper.getInstance(Constants.APPLICATION_FAVORITES_PATH);
			ch.setProperties(favs);
			ch.storeProperties();
		} catch (Exception e) {
			System.out.println("Cannot write favorites to file.");
			e.printStackTrace();
		}
	}

	public void storeFavoritesToObjectFile() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Constants.APPLICATION_FAVORITES_PATH));
			out.writeObject(items);
			out.flush();
			out.close();
		} catch (Exception e) {
			System.out.println("Cannot write favorites to file.");
		}
	}

	public void addItem(FavoriteItemVO item) {
		items.add(item);
	}

	public void addItem(String name, String path) {
		items.add(new FavoriteItemVO(name, path));
	}

	public FavoriteItemVO getItem(int pos) {
		return items.get(pos);
	}

	public void setItem(int pos, FavoriteItemVO item) {
		items.set(pos, item);
	}

}
