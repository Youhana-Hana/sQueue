package mobi.MobiSeeker.sQueue.activites;



import java.util.ArrayList;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.connection.ConnectionConstant;
import mobi.MobiSeeker.sQueue.connection.NodeManager;
import mobi.MobiSeeker.sQueue.connection.NodeObject;
import mobi.MobiSeeker.sQueue.connection.ServiceManger;
import mobi.MobiSeeker.sQueue.connection.onConnected;
import mobi.MobiSeeker.sQueue.data.Entry;
import mobi.MobiSeeker.sQueue.data.Message;
import mobi.MobiSeeker.sQueue.data.Settings;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class Queue extends BaseActivity implements ActionBar.TabListener ,onConnected{

	public static final String Local = "local";
	public static final String Remote = "remote";
	public static final String New_Messag_Action = "mobi.MobiSeeker.sQueue.NEW_MESSAGE";
	public static final String View_Contact_Action = "mobi.MobiSeeker.sQueue.VIEW_CONTACT";
	private static final String Remove_Conversation = "mobi.MobiSeeker.sQueue.REMOVE_CONTAC1T";
	public final static int REQ_CODE_PICK_IMAGE_SETTINS = 1;
	public final static int REQ_CODE_PICK_IMAGE = 2;
	public final static int CAMERA_REQUEST = 3;
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	BroadcastReceiver receiver;
	IntentFilter intentFIlter;
	String nodeName;

	private ArrayList<NodeObject> nodesObjects;
	
	private ArrayList<Conversation> conversations;
	
	/*
	 * Connection Manger
	 * */
	
	ServiceManger manger;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promotions);
		nodesObjects=new ArrayList<NodeObject>();
		conversations=new ArrayList<Conversation>();
	    manger=ServiceManger.getInstance(this,true,this);
        manger.startService();
        manger.bindChordService();

		this.nodeName = "NodeName"; // need to get this from chrod nodeManager
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(),this, this.nodeName);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position)
		{
			actionBar.setSelectedNavigationItem(position);
		}
		});
		this.addTabs(actionBar);
		this.intentFIlter = new IntentFilter();

		this.intentFIlter.addAction(Queue.New_Messag_Action);
		this.intentFIlter.addAction(Queue.View_Contact_Action);
		this.intentFIlter.addAction(Queue.Remove_Conversation);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleReceiverIntent(intent);
			}
		};
	}

	protected void handleReceiverIntent(Intent intent) {
		if (intent.getAction().equalsIgnoreCase(Queue.New_Messag_Action)) {
			Message message = (Message) intent.getSerializableExtra("message");
			addMessage(message);
			return;
		} else if (intent.getAction().equalsIgnoreCase(
				Queue.View_Contact_Action)) {
			Entry entry = (Entry) intent.getSerializableExtra("entry");
			String tabName = getEntryTabName(entry);
			AddConversationTab(entry, tabName, true);
			return;
		} else if (intent.getAction().equalsIgnoreCase(
				Queue.Remove_Conversation)) {
			int position = intent.getIntExtra("tabPosition", -1);
			removeTab(position);
			return;
		}
	}

	private Tab getTabForMessage(ActionBar actionBar, Message message) {
		String tabName = this.getEntryTabName(message.getFrom());
		Tab tab = this.getTabByName(actionBar, tabName);
		if (tab != null) {
			return tab;
		}

		boolean focus = mSectionsPagerAdapter.getCount() == 2;
		String allTab = this.getString(R.string.all);
		return this.AddConversationTab(message.getFrom(), allTab, focus);
	}

	private void addMessage(Message message) {
		ActionBar actionBar = getActionBar();
		Tab tab = getTabForMessage(actionBar, message);
		Conversation conversation = (Conversation) mSectionsPagerAdapter
				.instantiateItem(mViewPager, tab.getPosition());
		conversation.addRemoteMessage(message);
	}

	private Tab AddConversationTab(Entry entry, String tabName, boolean focus) {
		ActionBar actionBar = getActionBar();
		Tab tab = getTabByName(actionBar, tabName);
		Conversation covnersation=null;
		if (tab == null) {
			tab = actionBar.newTab().setText(tabName)
					.setIcon(Drawable.createFromPath(entry.getLogo()))
					.setTabListener(this);

			int index = actionBar.getTabCount() - 1;
			this.mSectionsPagerAdapter.AddPageIn(index);
			this.mSectionsPagerAdapter.notifyDataSetChanged();
			actionBar.addTab(tab, index);
			 covnersation = (Conversation) mSectionsPagerAdapter
					.instantiateItem(mViewPager, index);
			covnersation.addEntry(entry);
			conversations.add(covnersation);
			if (focus) {
				actionBar.setSelectedNavigationItem(index);
			}
		} else {
			if (focus) {
				actionBar.setSelectedNavigationItem(tab.getPosition());
			}
		}
		tab.setTag(covnersation);
		return tab;
	}

	private String getEntryTabName(Entry entry) {
		String tabNme = entry.getName().isEmpty() ? entry.getNodeObject().nodeValue : entry.getNodeObject().nodeValue;
		return tabNme;
	}

	private Tab getTabByName(ActionBar actionBar, String name) {
		int tabs = actionBar.getTabCount();

		for (int index = 0; index < tabs; index++) {
			if (actionBar.getTabAt(index).getText().toString()
					.compareToIgnoreCase(name) == 0) {
				return actionBar.getTabAt(index);
			}
		}

		return null;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, this.intentFIlter);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		tab.setTag(null);
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

		if (this.isPermitTabs(tab.getPosition())) {
			return;
		}

		Integer count = (Integer) tab.getTag();
		if (count == null) {
			tab.setTag(1);
			return;
		}

		if (count == 2) {
			Intent intent = new Intent(Queue.Remove_Conversation);
			intent.putExtra("tabPosition", tab.getPosition());
			sendBroadcast(intent);
			return;
		}

		tab.setTag(++count);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
		case (MotionEvent.ACTION_DOWN):
			removeTab(getActionBar().getSelectedNavigationIndex());
			return true;
		case (MotionEvent.ACTION_CANCEL):
			removeTab(getActionBar().getSelectedNavigationIndex());
			return true;
		default:
			return super.onTouchEvent(event);
		}
	}

	public void pickLogo(View view) {
		this.pickImageFromGallery(REQ_CODE_PICK_IMAGE_SETTINS);
	}

	public void pickImage(View view) {
		this.pickImageFromGallery(REQ_CODE_PICK_IMAGE);
	}

	private void pickImageFromGallery(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(intent, requestCode);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {

		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case Queue.CAMERA_REQUEST: {
			if (resultCode != RESULT_OK) {
				return;
			}

			if (imageReturnedIntent == null) {
				return;
			}

			String fileName = this.getImagePathFromIntent(imageReturnedIntent);
			
			Conversation conversation = (Conversation) mSectionsPagerAdapter
					.instantiateItem(mViewPager, getActionBar().getSelectedNavigationIndex());
			
			conversation.sendFile(fileName);
			break;
		}
		case REQ_CODE_PICK_IMAGE:
		case REQ_CODE_PICK_IMAGE_SETTINS:
			if (resultCode != RESULT_OK) {
				return;
			}

			if (imageReturnedIntent == null) {
				return;
			}

			ImageView image = null;

			if (requestCode == REQ_CODE_PICK_IMAGE) {
				image = (ImageView) findViewById(R.id.image);
			} else {
				image = (ImageView) findViewById(R.id.logo);
			}

			if (image == null) {
				return;
			}

			String imagePath = getImageFromGallery(imageReturnedIntent, image);

			image.setTag(imagePath);

			if (requestCode == REQ_CODE_PICK_IMAGE) {
				return;
			}

			Settings settings = new Settings(this);
			settings.setLogo(imagePath);
		}
	}

	private String getImageFromGallery(Intent imageReturnedIntent,
			ImageView image) {

		String path = this.getImagePathFromIntent(imageReturnedIntent);
		image.setImageBitmap(BitmapFactory.decodeFile(path));
		return path;
	}

	private String getImagePathFromIntent(Intent imageReturnedIntent) {

		Uri selectedImage = imageReturnedIntent.getData();

		String[] filePathColumn = { android.provider.MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);

		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String imagePath = cursor.getString(columnIndex);
		cursor.close();

		return imagePath;
	}
	
	private void addTabs(final ActionBar actionBar) {
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	private void removeTab(int position) {
		if (this.isPermitTabs(position)) {
			return;
		}

		ActionBar actionBar = getActionBar();
		mSectionsPagerAdapter.takePageOut(position);
		actionBar.getTabAt(position - 1).setTag(null);
		actionBar.setSelectedNavigationItem(position - 1);
		actionBar.removeTabAt(position);
		mSectionsPagerAdapter.notifyDataSetChanged();
	}

	private boolean isPermitTabs(int position) {
		return position == 0
				|| position == mSectionsPagerAdapter.getCount() - 1;
	}

	@Override
	public void connected() {
		// TODO Auto-generated method stub
		
	}
	
	public void getNodeList()
	{
		manger.getmChordService().sendDataToAll(NodeManager.CHORD_API_CHANNEL,"welcome".getBytes(), ConnectionConstant.GET_DEVICE_NAME);
		
	}
	
	public void refreshNodeList()
	{
		mSectionsPagerAdapter.refreshNodeList(nodesObjects);
		
	}
	
	
	
	
	public void checkIsTabExisting(String node, String channel, String message,
			String MessageType)
	{
	
		for(int i=0;i<conversations.size();i++)
		{
			Conversation conversion=conversations.get(i);
			Entry entry=conversion.getCurrentEntry(node);
			
			if(entry!=null)
			{
				entry.setName(getNodeObjectByNodename(node).nodeValue);
				entry.setNodeObject(getNodeObjectByNodename(node));
				if(entry.getNodeObject().nodeName.equalsIgnoreCase(node))
				{
					
				Message messageObject=new Message(entry, message, entry.getLogo(),null);
				conversion.addRemoteMessage(messageObject);
				return;
				}
				
			}
			
		}
		NodeObject nodeObject=getNodeObjectByNodename(node);
		if(nodeObject!=null){
		Entry entry=new Entry(nodeObject.nodeName,nodeObject.nodeValue,nodeObject.nodeImagePath,null);
		entry.setName(getNodeObjectByNodename(node).nodeValue);
		entry.setNodeObject(nodeObject);
		Tab tab=AddConversationTab(entry, nodeObject.nodeValue, true);
		if(tab.getTag()!=null){
			Message messageObject=new Message(entry, message, entry.getLogo(),null);

			((Conversation)tab.getTag()).addRemoteMessage(messageObject);
			
		}
		
		//checkIsTabExisting(node, channel, message, MessageType);
		}
		
	}
	
	
	private NodeObject getNodeObjectByNodename(String nodename)
	{
		for(int i=0;i<nodesObjects.size();i++)
		{
			if(nodename.equalsIgnoreCase(nodesObjects.get(i).nodeName))
			{
				return nodesObjects.get(i);
			}
		}
		return null;
	}
	
	@Override
	public void onReceiveMessage(String node, String channel, String message,
			String MessageType) 
	{
		// TODO Auto-generated method stub
		
		if (MessageType.equalsIgnoreCase(ConnectionConstant.SEND_MESSAGE_TOALL)) 
		{
			runNotification(message);
			NodeObject nodeObject=getNodeObjectByNodename(node);
			if(nodeObject!=null){
			Entry entry=new Entry(nodeObject.nodeName,nodeObject.nodeValue,nodeObject.nodeImagePath,null);
			entry.setName(nodeObject.nodeValue);
			entry.setNodeObject(nodeObject);
			Message messageObject=new Message(entry, message, entry.getLogo(),null);
			mSectionsPagerAdapter.AddRemoteMessageToNodeList(messageObject);
			}

				
		}else
		if (MessageType.equalsIgnoreCase(ConnectionConstant.SEND_MESSAGE))
		{
			runNotification(message);
			checkIsTabExisting(node, channel, message, MessageType);
			
		}

		if (MessageType.equalsIgnoreCase(ConnectionConstant.GET_DEVICE_NAME)) {
			try {
				ServiceManger.getInstance(this, false,null).sendData(new Settings(this).getUserName(),
						ConnectionConstant.MY_DEVICE_NAME, node);
				
				
			} catch (Exception ee) {
				ServiceManger.getInstance(this, false,null).sendData(
						android.os.Build.MODEL,
						ConnectionConstant.MY_DEVICE_NAME, node);
			}
		}
		else

			if (MessageType.equalsIgnoreCase(ConnectionConstant.MY_DEVICE_NAME)) 
			{
				
				NodeObject nodeObject=new NodeObject();
				nodeObject.nodeName=node;
				nodeObject.nodeValue=message;
				nodesObjects.add(nodeObject);
				refreshNodeList();
			}

		
	}
	

	
	@Override
	public void onFileWillReceive(String node, String channel, String fileName,
			String exchangeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileProgress(boolean bSend, String node, String channel,
			int progress, String exchangeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileCompleted(int reason, String node, String channel,
			String exchangeId, String fileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNodeEvent(String node, String channel, boolean bJoined) {
		// TODO Auto-generated method stub
		
		if(bJoined)
		{
			ServiceManger.getInstance(this, false,null).sendData(new Settings(this).getUserName(),
					ConnectionConstant.MY_DEVICE_NAME, node);
			
		}else
		{
			for(int i=0;i<nodesObjects.size();i++)
			{
				if(nodesObjects.get(i).nodeName.equalsIgnoreCase(node))
				{
					nodesObjects.remove(i);
					break;
				}
				
			}
			refreshNodeList();
		}
		

	}

	@Override
	public void onNetworkDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateNodeInfo(String nodeName, String ipAddress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectivityChanged() {
		// TODO Auto-generated method stub

	}
	Ringtone r ;
	Vibrator   vibrator;
		@SuppressLint("NewApi")
		public void runNotification(String message)
		{
			try{
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.launcher)
			        .setContentTitle(message)
			        .setContentText("");
			
			Intent resultIntent = new Intent(this, Queue.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
			        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(pIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(0, mBuilder.build());
			}catch(Exception ee){ee.printStackTrace();}
			
			
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			r.play();
			
			if(new Settings(this).isVibrate()){
			 //Set the pattern for vibration   
	        long pattern[]={0,200,100,300,400};
	        //Start the vibration
	        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	        //start vibration with repeated count, use -1 if you don't want to repeat the vibration
	        vibrator.vibrate(pattern, -1);        
			}
		}
	
}
