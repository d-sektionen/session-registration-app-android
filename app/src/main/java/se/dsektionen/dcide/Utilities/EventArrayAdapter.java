package se.dsektionen.dcide.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import se.dsektionen.dcide.JsonModels.Event;
import se.dsektionen.dcide.R;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class EventArrayAdapter extends BaseAdapter {

    private ArrayList<Event> events;
    private Context context;

    public EventArrayAdapter(ArrayList<Event> events, Context context){
        this.events = events;
        this.context = context;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.meeting_layout,null);
        }

        TextView name = convertView.findViewById(R.id.meeting_name_textview);
        TextView section = convertView.findViewById(R.id.meeting_section_textview);

        convertView.setTag(events.get(position));
        name.setText(events.get(position).getName());
        section.setText(events.get(position).getSection().getName());


        return convertView;
    }
}
