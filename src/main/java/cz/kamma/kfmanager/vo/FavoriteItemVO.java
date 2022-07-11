package cz.kamma.kfmanager.vo;

import java.io.Serializable;

public class FavoriteItemVO implements Serializable {

	private static final long serialVersionUID = 2936105015300556900L;

	private String name;
	private String path;

	public FavoriteItemVO(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
