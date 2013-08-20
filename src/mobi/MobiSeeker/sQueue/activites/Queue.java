package mobi.MobiSeeker.sQueue.activites;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.Entry;
import mobi.MobiSeeker.sQueue.data.Message;
import mobi.MobiSeeker.sQueue.data.Settings;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class Queue extends FragmentActivity implements ActionBar.TabListener {

	public static final String Local = "local";
	public static final String Remote = "remote";
	public static final String New_Messag_Action = "mobi.MobiSeeker.sQueue.NEW_MESSAGE";
	public static final String View_Contact_Action = "mobi.MobiSeeker.sQueue.VIEW_CONTACT";
	private static final String Remove_Conversation = "mobi.MobiSeeker.sQueue.REMOVE_CONTAC1T";
	private final int REQ_CODE_PICK_IMAGE_SETTINS = 1;
	private final int REQ_CODE_PICK_IMAGE = 2;

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	BroadcastReceiver receiver;
	IntentFilter intentFIlter;
	String nodeName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promotions);

		this.nodeName = "NodeName"; // need to get this from chrod nodeManager
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(),
				this, this.nodeName);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
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
		if (tab == null) {
			tab = actionBar.newTab().setText(tabName)
					.setIcon(Drawable.createFromPath(entry.getLogo()))
					.setTabListener(this);

			int index = actionBar.getTabCount() - 1;
			this.mSectionsPagerAdapter.AddPageIn(index);
			this.mSectionsPagerAdapter.notifyDataSetChanged();
			actionBar.addTab(tab, index);
			Conversation covnersation = (Conversation) mSectionsPagerAdapter
					.instantiateItem(mViewPager, index);
			covnersation.addEntry(entry);
			if (focus) {
				actionBar.setSelectedNavigationItem(index);
			}
		} else {
			if (focus) {
				actionBar.setSelectedNavigationItem(tab.getPosition());
			}
		}

		return tab;
	}

	private String getEntryTabName(Entry entry) {
		String tabNme = entry.getName().isEmpty() ? entry.getNodeName() : entry
				.getName();
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

		Uri selectedImage = imageReturnedIntent.getData();

		String[] filePathColumn = { android.provider.MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);

		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String imagePath = cursor.getString(columnIndex);
		cursor.close();

		image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
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
}
