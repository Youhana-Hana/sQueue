package mobi.MobiSeeker.sQueue.activites;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.Adapter;
import mobi.MobiSeeker.sQueue.data.Entry;
import mobi.MobiSeeker.sQueue.data.Repository;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class NodesList extends ListFragment {

	protected Repository repository;
	protected Adapter adapter;
	ImageView send = null;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			this.repository = new Repository(Queue.Remote);
			PopulateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.nodes_list, container, false);

		this.send = (ImageView) rootView.findViewById(R.id.send);
		this.send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});

		return rootView;
	}

	 public void sendMessage() {
		 // send message to all nodes
	    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Entry entry = adapter.getItem(position);
		Brodcast(entry, Queue.View_Remote_Promotion_Action);
	}
	
	private void Brodcast(Entry entry, String action) {
		Context context = getActivity().getBaseContext();
		Intent intent = new Intent(action);
		if(entry != null) {
			intent.putExtra("entry", entry);
		}
		
		context.sendBroadcast(intent);
	}

	private void PopulateList() throws Exception {
		Context context = getActivity().getBaseContext();
		this.adapter = new Adapter(context, R.layout.entry,
				repository.getEntries(context));
		setListAdapter(this.adapter);
	}
}
