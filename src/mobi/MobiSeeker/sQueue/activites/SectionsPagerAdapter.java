package mobi.MobiSeeker.sQueue.activites;

import java.util.Locale;

import mobi.MobiSeeker.sQueue.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

	private Context context;
	private int maxPagesCount;
	private boolean pagesCountChanged;

	public SectionsPagerAdapter(FragmentManager fm, Context context,
			String nodeName) {
		super(fm);
		this.context = context;
		this.maxPagesCount = 2;
		this.pagesCountChanged = false;
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			return getQueueList();
		}

		if (position == this.maxPagesCount - 1) {
			return new SettingsFragment();
		}

		return new Covnersation();
	}

	private Fragment getQueueList() {
		return new NodesList();
	}

	@Override
	public int getCount() {
		return this.maxPagesCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();

		if (position == 0) {
			return this.context.getString(R.string.title_section1).toUpperCase(
					l);
		}

		if (position == getCount() - 1) {
			return this.context.getString(R.string.action_settings)
					.toUpperCase(l);
		}

		return null;
	}

	@Override
	public int getItemPosition(Object object) {
		if (this.pagesCountChanged) {
			return POSITION_NONE;
		}
		return POSITION_UNCHANGED;
	}

	public void setMaxPagesCount(int count) {
		this.maxPagesCount = count;
	}

	public void AddPageIn() {
		this.maxPagesCount++;
		this.pagesCountChanged = true;
	}

	public void takePageOut() {
		this.maxPagesCount--;
		this.pagesCountChanged = true;
	}
}
