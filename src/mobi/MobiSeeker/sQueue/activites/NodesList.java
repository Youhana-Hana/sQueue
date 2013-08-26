package mobi.MobiSeeker.sQueue.activites;
import java.util.ArrayList;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.connection.ConnectionConstant;
import mobi.MobiSeeker.sQueue.connection.NodeManager;
import mobi.MobiSeeker.sQueue.connection.NodeObject;
import mobi.MobiSeeker.sQueue.connection.ServiceManger;
import mobi.MobiSeeker.sQueue.data.Adapter;
import mobi.MobiSeeker.sQueue.data.Entry;
import mobi.MobiSeeker.sQueue.data.Message;
import mobi.MobiSeeker.sQueue.data.MessageAdapter;
import mobi.MobiSeeker.sQueue.data.Repository;
import mobi.MobiSeeker.sQueue.data.Settings;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class NodesList extends ListFragment
{
	protected Repository repository;
	protected Adapter adapter;
	protected MessageAdapter messageAdapter;
	ImageView send = null;
	ListView messagetextView;
	EditText message_content;
	ArrayList<Message> messageList;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		messageList=new ArrayList<Message>();
		this.messageAdapter = new MessageAdapter(this.getActivity(), R.layout.message,
				this.messageList);
		View rootView = inflater
				.inflate(R.layout.nodes_list, container, false);
		this.send = (ImageView) rootView.findViewById(R.id.send);
		this.send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				sendMessage();
			}
		});
		message_content=(EditText) rootView.findViewById(R.id.message_content);
		messagetextView=(ListView) rootView.findViewById(R.id.messagetexts);
		return rootView;
	}


	
	public void addRemoteMessage(Message message) {

		this.messageList.add( message);
		this.messagetextView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		this.messagetextView.setStackFromBottom(true);
		setAdapter();
	}
	
	
	
	private void addLocalMessage() {
		Message message = new Message(new Entry(getActivity().getResources()
				.getString(R.string.me), "", "", null), this.message_content.getText()
				.toString(), new Settings(this.getActivity()).getLogo(),null);

		this.messageList.add( message);
		this.messagetextView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		this.messagetextView.setStackFromBottom(true);
		setAdapter();
	}
	
	
	public void setAdapter()
	{
		
		this.messagetextView.setAdapter(this.messageAdapter);
		
	}

	 public void addRemoteMessage()
	 {
		 if(message_content.getText().toString().length()>0)
		 {
			 
		 ServiceManger manger=ServiceManger.getInstance(null, false, null);
		 if(manger.getmChordService()!=null)
		 {
			 addLocalMessage();
			 manger.getmChordService().sendDataToAll(NodeManager.CHORD_API_CHANNEL, message_content.getText().toString().getBytes(), ConnectionConstant.SEND_MESSAGE_TOALL);
		 }else
		 {
			Toast.makeText(this.getActivity(),"Please Check your Connection", 10000).show(); 
		 }
		 }
	   }

	
	 public void sendMessage()
	 {
		 if(message_content.getText().toString().length()>0)
		 {
			 
		 ServiceManger manger=ServiceManger.getInstance(null, false, null);
		 if(manger.getmChordService()!=null)
		 {
			 addLocalMessage();
			 manger.getmChordService().sendDataToAll(NodeManager.CHORD_API_CHANNEL, message_content.getText().toString().getBytes(), ConnectionConstant.SEND_MESSAGE_TOALL);
				message_content.setText("");

		 }else
		 {
			Toast.makeText(this.getActivity(),"Please Check your Connection", 10000).show(); 
		 }
		 }
	   }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		NodeObject entry = adapter.getItem(position);
		Brodcast(entry, Queue.View_Contact_Action);
	}
	
	private void Brodcast(NodeObject nodeObject, String action) {
		Context context = getActivity().getBaseContext();
		Intent intent = new Intent(action);
		
		Entry entry=new Entry(nodeObject.nodeName,nodeObject.nodeValue,nodeObject.nodeImagePath,null);
		entry.setNodeObject(nodeObject);
		if(entry != null) {
			intent.putExtra("entry", entry);
		}
		
		context.sendBroadcast(intent);
	}

	public void PopulateList(ArrayList<NodeObject> nodesObject)  {
		try{
		
			
			
		Context context = getActivity().getBaseContext();
		this.adapter = new Adapter(context, R.layout.entry,
				nodesObject);
		setListAdapter(this.adapter);
		}catch(Exception ee){ee.printStackTrace();}
	}
}
