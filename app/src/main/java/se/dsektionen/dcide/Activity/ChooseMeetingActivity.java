package se.dsektionen.dcide.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.R;
import se.dsektionen.dcide.Requests.JsonArrayRequestCallback;
import se.dsektionen.dcide.Requests.MeetingRequestCallback;
import se.dsektionen.dcide.Utilities.MeetingArrayAdapter;

/**
 * Created by Gustav on 2017-11-13.
 */

public class ChooseMeetingActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, OnClickListener{

    ImageButton close;
    SharedPreferences preferences;
    ListView meetingListview;
    DCideApp mApp;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_session);
        meetingListview = findViewById(R.id.meeting_listView);
        mApp = DCideApp.getInstance();
        setTitle("");
        final Context context = this;
        mApp.getUserSessionManager().getMeetings(new MeetingRequestCallback() {
            @Override
            public void onGetMeetings(ArrayList<Meeting> meetings) {
                MeetingArrayAdapter adapter = new MeetingArrayAdapter(meetings, context);
                meetingListview.setAdapter(adapter);
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
