package mobi.MobiSeeker.sQueue.activites;

import java.util.Locale;

import mobi.MobiSeeker.sQueue.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

	private Context context;

	public SectionsPagerAdapter(FragmentManager fm, Context context, String nodeName) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position) {
		case 0:	
			return getPromotions();
		default:
			return new SettingsFragment();
		}
	}

	private Fragment getPromotions() {
			return new PromotionsList();
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return this.context.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return this.context.getString(R.string.action_settings).toUpperCase(l);
		}
		return null;
	}
}
