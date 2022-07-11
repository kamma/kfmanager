package cz.kamma.kfmanager.vo;

public class SearchOptionsVO {

	private String searchFor;
	private String searchIn;
	private boolean caseSensitive;
	private boolean regExp;
	private boolean inZip;

	public SearchOptionsVO(String searchFor, String searchIn, boolean caseSensitive, boolean regExp, boolean inZip) {
		this.searchFor = searchFor;
		this.searchIn = searchIn;
		this.regExp = regExp;
		this.inZip = inZip;
		this.caseSensitive = caseSensitive;
	}

	public boolean isInZip() {
		return inZip;
	}

	public void setInZip(boolean inZip) {
		this.inZip = inZip;
	}

	public boolean isRegExp() {
		return regExp;
	}

	public void setRegExp(boolean regExp) {
		this.regExp = regExp;
	}

	public String getSearchFor() {
		return searchFor;
	}

	public void setSearchFor(String searchFor) {
		this.searchFor = searchFor;
	}

	public String getSearchIn() {
		return searchIn;
	}

	public void setSearchIn(String searchIn) {
		this.searchIn = searchIn;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

}
