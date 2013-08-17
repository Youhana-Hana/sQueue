package mobi.MobiSeeker.sQueue.data;

import java.util.List;

import mobi.MobiSeeker.sQueue.R;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adapter extends ArrayAdapter<Entry> {

    static class ViewHolder {
        public TextView name;
        public ImageView logo;
    }

    protected Context context;
    protected List<Entry> entries;
    int resourceId;
    public Adapter(Context context, int resourceId, List<Entry> entries) {
        super(context, resourceId, entries);
        this.context = context;
        this.resourceId = resourceId;
        this.entries = entries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(resourceId, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.entryTitle);
            viewHolder.logo = (ImageView) rowView.findViewById(R.id.entryLogo);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        Entry entry = this.entries.get(position);

        String name = entry.getName();
        if (name.isEmpty() || name == null) {
        	name = entry.getNodeName();
        }
        
        viewHolder.name.setText(name);
        
        if (viewHolder.logo != null) {
        	viewHolder.logo.setImageURI(Uri.parse(entry.getLogo()));
        }
        
        return rowView;
    }
 }
