package mobi.MobiSeeker.sQueue.activites;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.Entry;
import mobi.MobiSeeker.sQueue.data.Settings;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

public class Queue extends FragmentActivity implements
		ActionBar.TabListener {

	public static final String Local = "local";
	public static final String Remote = "remote";
	public static final String View_Promotion_Action = "mobi.MobiSeeker.sQueue.VIEW_PROMOTION";
	public static final String View_Remote_Promotion_Action = "mobi.MobiSeeker.sQueue.VIEW_REMOTE_PROMOTION";
	public static String Add_New_Promotion_Action = "mobi.MobiSeeker.sQueue.ADD_NEW_PROMOTION";
	public static String Delete_Local_Promotion_Action = "mobi.MobiSeeker.sQueue.DELETE_LOCAL_PROMOTION";
	public static String View_local_Promotions_Action = "mobi.MobiSeeker.sQueue.VIEW_LOCAL_PROMOTIONS";
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

		this.intentFIlter = new IntentFilter(
				Queue.Add_New_Promotion_Action);

		this.intentFIlter.addAction(Queue.View_local_Promotions_Action);
		this.intentFIlter.addAction(Queue.View_Promotion_Action);
		this.intentFIlter.addAction(Queue.View_Remote_Promotion_Action);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleReceiverIntent(intent);
			}
		};
	}

	protected void handleReceiverIntent(Intent intent) {
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
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void deleteEntry(View view) {
		Entry entry = (Entry) view.getTag();
		if (entry == null) {
			return;
		}

		PromotedList promotedList = (PromotedList) mSectionsPagerAdapter
				.instantiateItem(mViewPager, 1);
		promotedList.delete(entry);
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
}
