package mobi.MobiSeeker.sQueue.data;

import java.io.Serializable;

import com.google.gson.Gson;

public class Message implements Serializable {

	public enum MessageType{
		text,
		RequestImage,
		RequestVideo
	};
	
	private static final long serialVersionUID = 1L;

	public Message(Entry from, String content, String logo, MessageType type) {

		this.from = from;
		this.content = content;
		this.logo = logo;
		this.type = type;
	}

	public Entry getFrom() {
		return this.from;
	}

	public String getContent() {
		return toDefault(this.content);
	}

	public String getLogo() {
		return this.logo;
	}
	
	public MessageType getType() {
		return this.type;
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

	public void setName(Entry from) {
		this.from = from;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	public void setType(MessageType type) {
		this.type =type;
	}
	
	private Entry from;
	private String content;
	private String logo;
	private MessageType type;
}
