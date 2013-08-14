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
        public TextView title;
        public TextView text;
        public ImageView logo;
        public ImageView image;
        public ImageView delete;
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
            viewHolder.title = (TextView) rowView.findViewById(R.id.entryTitle);
            viewHolder.text = (TextView) rowView.findViewById(R.id.entrySummary);
            viewHolder.logo = (ImageView) rowView.findViewById(R.id.entryLogo);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.entryImage);
            viewHolder.delete = (ImageView) rowView.findViewById(R.id.entryDelete);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        Entry entry = this.entries.get(position);

        viewHolder.title.setText(entry.getTitle());
        viewHolder.text.setText(entry.getText());
        
        if (viewHolder.logo != null) {
        	viewHolder.logo.setImageURI(Uri.parse(entry.getLogo()));
        }
        
        viewHolder.image.setImageURI(Uri.parse(entry.getImagePath()));
        viewHolder.delete.setTag(entry);
        return rowView;
    }
 }
