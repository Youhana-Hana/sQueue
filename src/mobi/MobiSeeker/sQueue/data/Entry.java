package mobi.MobiSeeker.sQueue.data;

import java.io.Serializable;

import com.google.gson.Gson;

public class Entry implements Serializable {

	private static final long serialVersionUID = 1L;

	public Entry(String name, String logo) {

		this.name = name;
		this.logo = logo;
		}

	public String getName() {
		return toDefault(this.name);
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

	public void setName(String name) {
		this.name = name;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	private String name;
	private String logo;
	}
