package mobi.MobiSeeker.sQueue.activites;

import java.util.ArrayList;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.connection.ConnectionConstant;
import mobi.MobiSeeker.sQueue.connection.NodeManager;
import mobi.MobiSeeker.sQueue.connection.ServiceManger;
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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;

public class Conversation extends ListFragment {

	private ImageView send = null;
	private Messages messages = null;
	private MessageAdapter adapter = null;
	private ArrayList<Entry> entries = null;
	private ArrayList<Message> messagesList;
	private EditText content;
	private Settings settings;

	public Conversation() {
		this.entries = new ArrayList<Entry>();
		this.messages = new Messages();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			this.settings = new Settings(this.getActivity().getBaseContext());
			PopulateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.conversation, container,
				false);

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

	public void addRemoteMessage(Message message) {
		this.messagesList.add( message);
		this.adapter.notifyDataSetChanged();
	}

	public void addEntry(Entry entry) {
		this.entries.add(entry);
	}

	public boolean isEntryHere(Entry entry) {
		for (Entry item : this.entries) {
			if (item.getName().equalsIgnoreCase(entry.getName())) {
				return true;
			}
		}

		return false;
	}

	private void addLocalMessage() {
		Message message = new Message(new Entry(getActivity().getResources()
				.getString(R.string.me), "", "", null), this.content.getText()
				.toString(), this.settings.getLogo());

		this.messagesList.add( message);
		this.getListView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		this.getListView().setStackFromBottom(true);
		this.adapter.notifyDataSetChanged();
	}

	private void sendRemoteMessage() {
		
		ServiceManger manger=ServiceManger.getInstance(null, false, null);
		if(manger.getmChordService()!=null)
		{
			for(Entry entry:entries){
			manger.getmChordService().sendData(NodeManager.CHORD_API_CHANNEL, this.content.getText()
					.toString().getBytes(), entry.getNodeObject().nodeName, ConnectionConstant.SEND_MESSAGE);
			}
			this.content.setText("");		
	
		}
		
	}

	private void PopulateList() throws Exception {
		Context context = getActivity().getBaseContext();
		this.messagesList = this.messages.getEntries();
		this.adapter = new MessageAdapter(context, R.layout.message,
				this.messagesList);
		setListAdapter(this.adapter);
	}

	public int getMessagesCount() {
		return this.messagesList.size();
	}
	
	public Entry getCurrentEntry(String nodeName)
	{
		for(Entry entry:entries)
		{
			if(entry.getNodeObject().nodeName.equalsIgnoreCase(nodeName))
				return entry;
				
		}

		return null;
	}
	
}
