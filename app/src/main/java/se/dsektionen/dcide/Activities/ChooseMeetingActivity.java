package se.dsektionen.dcide.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.R;
import se.dsektionen.dcide.Requests.Callbacks.MeetingRequestCallback;
import se.dsektionen.dcide.Utilities.MeetingArrayAdapter;

/**
 * Created by Gustav on 2017-11-13.
 */

public class ChooseMeetingActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, OnClickListener{

    ImageButton close;
    SharedPreferences preferences;
    ListView meetingListview;
    DCideApp mApp;
    ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_session);
        meetingListview = findViewById(R.id.meeting_listView);
        progressBar = findViewById(R.id.meeting_loading_view);
        mApp = DCideApp.getInstance();
        setTitle("");
        final Context context = this;
        mApp.getUserSessionManager().getMeetings(new MeetingRequestCallback() {
            @Override
            public void onGetMeetings(ArrayList<Meeting> meetings) {
                MeetingArrayAdapter adapter = new MeetingArrayAdapter(meetings, context);
                meetingListview.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFail() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context,"Nätverksfel", Toast.LENGTH_LONG).show();
            }
        });

        meetingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mApp.getMeetingManager().setMeeting((Meeting) view.getTag());
                finish();
            }
        });


        this.setFinishOnTouchOutside(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onBackStackChanged() {
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == close.getId()){
            finish();
        }

    }
}
