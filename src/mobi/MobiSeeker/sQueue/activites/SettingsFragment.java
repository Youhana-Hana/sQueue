package mobi.MobiSeeker.sQueue.activites;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.data.Settings;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.picture, container, false);
		Settings settings = new Settings(getActivity());
		String imagePath = settings.getLogo();
		if (imagePath != null && !imagePath.isEmpty())
		{
			ImageView logo = (ImageView) view.findViewById(R.id.logo);
			if (logo != null) {
				logo.setImageURI(Uri.parse(imagePath));
			}
		}

		return view;
	}
}
