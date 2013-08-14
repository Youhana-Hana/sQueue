package mobi.MobiSeeker.sQueue.activites;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.Entry;
import mobi.MobiSeeker.sQueue.data.Repository;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class NewPromotion extends Fragment {

	Button entrySave = null;
	Button entrySaveCancel = null;
	EditText entryTitle = null;
	EditText entrySummary = null;
	ImageView entryImage = null;
	Entry entryFromIntent = null;
	String nodeName = null;

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.newpromotion, container,
				false);

		this.entryTitle = (EditText) rootView.findViewById(R.id.editTitle);
		this.entrySummary = (EditText) rootView.findViewById(R.id.editText);
		this.entryImage = (ImageView) rootView.findViewById(R.id.image);

		this.entrySave = (Button) rootView.findViewById(R.id.entrySave);
		this.entrySaveCancel = (Button) rootView
				.findViewById(R.id.entrySaveCancel);

		this.entrySave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (entryTitle.getText().toString().trim().isEmpty() || entrySummary.getText().toString().trim().isEmpty()) {
					warn();
				} else {
					save();
					goHome();
				}
			}
		});

		this.entrySaveCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goHome();
			}
		});

		this.SetVauesFromIntent();

		return rootView;
	}

	public void warn() {
     	AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(this.getResources().getString(R.string.add_promotion_error_alert_title));
        alertDialog.setMessage(this.getResources().getString(R.string.add_promotion_error_alert_text));
        alertDialog.setPositiveButton(this.getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.create().show();
    }
	private void SetVauesFromIntent() {
		Intent intent = getActivity().getIntent();
		Entry entry = (Entry) intent.getSerializableExtra("entry");
		if (entry == null) {
			return;
		}

		this.entryFromIntent = entry;

		this.entryTitle.setText(entry.getTitle());
		this.entrySummary.setText(entry.getText());
		String imageUrl = entry.getImagePath();
		if (imageUrl.isEmpty() || imageUrl == null) {
			return;
		}

		this.entryImage.setImageURI(Uri.parse(entry.getImagePath()));
	}

	void save() {
		Repository repository = new Repository(Queue.Local);
		Context context = getActivity().getBaseContext();
		try {
			repository.save(context, getEntry(context));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Entry getEntry(Context context) {

		String title = this.entryTitle.getText().toString();
		String content = this.entrySummary.getText().toString();

		String image = null;
		if (this.entryImage.getTag() != null) {
			image = (String) this.entryImage.getTag();
		}

		String logo = new mobi.MobiSeeker.sQueue.data.Settings(context)
				.getLogo();

		if (this.entryFromIntent == null) {
			return new Entry(title, content, logo, image, this.nodeName);
		}

		this.entryFromIntent.setTitle(title);
		this.entryFromIntent.setText(content);
		this.entryFromIntent.setLogo(logo);
		this.entryFromIntent.setImagePath(image);
		return this.entryFromIntent;
	}

	void goHome() {
		Context context = getActivity().getBaseContext();
		Intent intent = new Intent(Queue.View_local_Promotions_Action);
		context.sendBroadcast(intent);
	}
}
