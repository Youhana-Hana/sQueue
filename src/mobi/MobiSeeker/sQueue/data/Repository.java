package mobi.MobiSeeker.sQueue.data;

import java.util.ArrayList;

import android.content.Context;

public class Repository {

	ArrayList<Entry> entries;
	
	public Repository () {
		this.entries= new ArrayList<Entry>();
	}
	
	public int getCount() {
		return this.entries.size();
	}

	public ArrayList<Entry> getEntries() throws Exception {
		return this.entries;
	}

	public void clear(Context context) {
		this.entries.clear();
	}
	
	public void add(Entry entry) {
		 this.entries.add(entry);
	}
	
	public Entry get(int index) {
		 return this.entries.get(index);
	}
}
