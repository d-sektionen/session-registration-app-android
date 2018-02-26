package se.dsektionen.dcide.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.R;

/**
 * Created by gustavaaro on 2018-02-26.
 */

public class MeetingArrayAdapter extends BaseAdapter {

    private ArrayList<Meeting> meetings;
    private Context context;

    public MeetingArrayAdapter(ArrayList<Meeting> meetings, Context context){
        this.meetings = meetings;
        this.context = context;
    }

    @Override
    public int getCount() {
        return meetings.size();
    }

    @Override
    public Object getItem(int position) {
        return meetings.get(position);
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

        convertView.setTag(meetings.get(position));
        name.setText(meetings.get(position).getName());
        section.setText(meetings.get(position).getSection().getName());


        return convertView;
    }
}
