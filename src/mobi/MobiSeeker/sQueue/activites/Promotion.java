package mobi.MobiSeeker.sQueue.activites;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.Entry;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Promotion extends Fragment {
	TextView entryTitle = null;
	TextView entrySummary = null;
	ImageView entryImage = null;
	Entry entryFromIntent = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.promotion, container, false);

		this.entryTitle = (TextView) rootView
				.findViewById(R.id.promotion_title);
		this.entrySummary = (TextView) rootView
				.findViewById(R.id.promotion_text);
		this.entryImage = (ImageView) rootView
				.findViewById(R.id.promotion_image);

		this.getEntryFromIntent();
		return rootView;
	}

	private void getEntryFromIntent() {
		Intent intent = getActivity().getIntent();
		Entry entry = (Entry) intent.getSerializableExtra("entry");
		if (entry == null) {
			return;
		}

		this.entryFromIntent = entry;

	}

	void goHome() {
		Context context = getActivity().getBaseContext();
		Intent intent = new Intent(Queue.View_local_Promotions_Action);
		context.sendBroadcast(intent);
	}
	
	
}
