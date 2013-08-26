package mobi.MobiSeeker.sQueue.data;

import java.io.Serializable;

import mobi.MobiSeeker.sQueue.connection.NodeObject;

import android.location.Location;

import com.google.gson.Gson;

public class Entry implements Serializable {

	private static final long serialVersionUID = 1L;

	private NodeObject nodeObject;
	
	public Entry(String name, String nodeName, String logo, Location location) {

		this.name = name;
		this.nodeName = nodeName;
		this.logo = logo;
		this.location = location;
	}

	public String getName() {
		return toDefault(this.name);
	}

	public String getNodeName() {
		return toDefault(this.nodeName);
	}

	public String getLogo() {
		return toDefault(this.logo);
	}

	public Location getLocation() {
		return this.location;
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

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	private String name;
	private String nodeName;
	private String logo;
	private Location location;

	public NodeObject getNodeObject() {
		return nodeObject;
	}

	public void setNodeObject(NodeObject nodeObject) {
		this.nodeObject = nodeObject;
	}
	
	
	
}
