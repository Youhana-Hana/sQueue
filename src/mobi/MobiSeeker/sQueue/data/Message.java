package mobi.MobiSeeker.sQueue.data;

import java.io.Serializable;

import com.google.gson.Gson;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	public Message(String from, String content) {

		this.from = from;
		this.content = content;
		}

	public String getFrom() {
		return toDefault(this.from);
	}

	public String getContent() {
		return toDefault(this.content);
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

	public void setName(String from) {
		this.from = from;
	}

	public void setLogo(String content) {
		this.content = content;
	}

	private String from;
	private String content;

	public String getPicture() {
		// TODO Auto-generated method stub
		return null;
	}
	}
