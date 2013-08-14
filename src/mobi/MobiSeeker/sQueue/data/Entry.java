package mobi.MobiSeeker.sQueue.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import com.google.gson.Gson;

public class Entry implements Serializable {

	private static final long serialVersionUID = 1L;

	public Entry(String title, String text, String logo, String imagePath,
			String nodeName) {

		this.title = title;
		this.text = text;
		this.logo = logo;
		this.imagePath = imagePath;
		this.nodeName = nodeName;
		this.time = new Date().getTime();
	}

	public String getTitle() {
		return toDefault(this.title);
	}

	public String getText() {
		return toDefault(this.text);
	}

	public String getFileName() {
		return String.format(Locale.getDefault(), "%s_%d.entry", this.nodeName, this.time);
	}

	public long getTime() {
		return this.time;
	}

	public String getImagePath() {
		return toDefault(this.imagePath);
	}

	public String getLogo() {
		return toDefault(this.logo);
	}

	private String toDefault(String value) {
		if (value == null) {
			return "";
		}

		return value;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	private String title;
	private String text;
	private String imagePath;
	private String logo;
	private String nodeName;
	private long time;
}
