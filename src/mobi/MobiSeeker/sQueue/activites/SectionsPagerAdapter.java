package mobi.MobiSeeker.sQueue.activites;

import java.util.ArrayList;
import java.util.Locale;

import mobi.MobiSeeker.sQueue.R;
import mobi.MobiSeeker.sQueue.connection.NodeObject;
import mobi.MobiSeeker.sQueue.data.Message;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

	private Context context;
	private int maxPagesCount;
	private ArrayList<Fragment> items;
	
	NodesList nodeList;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context,
			String nodeName) {
		super(fm);
		this.context = context;
		this.maxPagesCount = 2;
		nodeList=new NodesList();
		this.items = new ArrayList<Fragment>();
		this.items.add(nodeList);
		this.items.add(new SettingsFragment());
	}

	@Override
	public Fragment getItem(int position) {
		return this.items.get(position);
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
		if (object instanceof mobi.MobiSeeker.sQueue.activites.NodesList) {
			return POSITION_UNCHANGED;
		}
		
		return POSITION_NONE;
	}

	public void AddPageIn(int index) {
		this.items.add(index, new Conversation());
		this.maxPagesCount++;
	}

	public void takePageOut(int index) {
		this.items.remove(index);
		this.maxPagesCount--;
	}
	
	public void refreshNodeList(ArrayList<NodeObject> list)
	{
	nodeList.PopulateList(list);	
	}
	
	public void AddRemoteMessageToNodeList(Message message)
	{
	nodeList.addRemoteMessage(message);	
	}
	
}
