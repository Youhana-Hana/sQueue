package mobi.MobiSeeker.sQueue.activites;

import java.util.ArrayList;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.Entry;
import mobi.MobiSeeker.sQueue.data.Message;
import mobi.MobiSeeker.sQueue.data.MessageAdapter;
import mobi.MobiSeeker.sQueue.data.Messages;
import mobi.MobiSeeker.sQueue.data.Settings;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

public class Covnersation extends ListFragment {

	private ImageView send = null;
	private Messages messages = null;
	private MessageAdapter adapter= null;
	private Entry entry = null;
	private ArrayList<Message> messagesList;
	private EditText content;
	private Settings settings;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			this.messages = new Messages();
			this.settings = new Settings(this.getActivity().getBaseContext());
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

		this.content = (EditText) rootView.findViewById(R.id.message_content);
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
		 this.addLocalMessage();
		 this.sendRemoteMessage();
	    }

	private void addLocalMessage() {
		Message message = new Message(getActivity().getResources().getString(R.string.me), 
				this.content.getText().toString(),
				this.settings.getLogo());
		
		this.messagesList.add(0, message);
		this.adapter.notifyDataSetChanged();
	}

	private void sendRemoteMessage() {
		 // send message to this node
		 // this.entry.getNodeName();
	}

	private void PopulateList() throws Exception {
		Context context = getActivity().getBaseContext();
		this.messagesList = this.messages.getEntries();
		this.adapter = new MessageAdapter(context, R.layout.message,
				this.messagesList);
		setListAdapter(this.adapter);
	}

	public void setEntry(Entry entry) {
		this.entry  = entry;
	}
}
