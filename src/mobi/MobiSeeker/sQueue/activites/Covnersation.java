package mobi.MobiSeeker.sQueue.activites;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.MessageAdapter;
import mobi.MobiSeeker.sQueue.data.Messages;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Covnersation extends ListFragment {

	ImageView send = null;
	Messages messages = null;
	MessageAdapter adapter= null;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			this.messages = new Messages();
			PopulateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.conversation, container, false);

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
		 // send message to this node
	    }

	private void PopulateList() throws Exception {
		Context context = getActivity().getBaseContext();
		this.adapter = new MessageAdapter(context, R.layout.message,
				this.messages.getEntries());
		setListAdapter(this.adapter);
	}
}
