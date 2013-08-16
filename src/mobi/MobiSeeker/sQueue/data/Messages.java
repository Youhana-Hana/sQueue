package mobi.MobiSeeker.sQueue.data;

import java.util.ArrayList;

public class Messages {
	ArrayList<Message> entries;

	public Messages() {
		this.entries = new ArrayList<Message>();
	}

	public int getCount() {
		return this.entries.size();
	}

	public ArrayList<Message> getEntries(){
		return this.entries;
	}

	public void clear() {
		this.entries.clear();
	}

	public void add(Message entry) {
		this.entries.add(entry);
	}

	public Message remove(int index) {
		return this.entries.remove(index);
	}
	
	public Message get(int index) {
		return this.entries.get(index);
	}
}
