package se.dsektionen.dcide.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.JsonModels.User;
import se.dsektionen.dcide.R;
import se.dsektionen.dcide.Requests.Callbacks.EventRequestCallback;
import se.dsektionen.dcide.Requests.Callbacks.UserResponseCallback;
import se.dsektionen.dcide.Utilities.Event;
import se.dsektionen.dcide.Utilities.EventArrayAdapter;

/**
 * Created by Gustav on 2017-11-13.
 */

public class ChooseMeetingActivity extends AppCompatActivity {

    ListView eventListView;
    DCideApp mApp;
    ProgressBar progressBar;
    TextView userInfo;
    Button logout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_session);
        eventListView = findViewById(R.id.meeting_listView);
        progressBar = findViewById(R.id.meeting_loading_view);
        logout = findViewById(R.id.logout_button);
        userInfo = findViewById(R.id.logged_in_user);

        mApp = DCideApp.getInstance();
        setTitle("");
        final Context context = this;
        mApp.getUserSessionManager().getMeetings(new EventRequestCallback() {
            @Override
            public void onGetEvents(ArrayList<Event> events) {
                EventArrayAdapter adapter = new EventArrayAdapter(events, context);
                eventListView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFail() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Nätverksfel", Toast.LENGTH_LONG).show();
            }
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mApp.getEventManager().setEvent((Meeting) view.getTag());
                setResult(RESULT_OK);
                finish();
            }
        });

        mApp.getUserSessionManager().getUser(new UserResponseCallback() {
            @Override
            public void onUserFetched(User user) {
                userInfo.setText("Inloggad som: " + user.getFirst_name() + " " + user.getLast_name());
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getUserSessionManager().clearSession();
                Intent intent = new Intent(mApp, LoginActivity.class);
                intent.putExtra("logged_out", true);
                startActivity(intent);
                finish();
            }
        });


        this.setFinishOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
        if (mApp.getEventManager().getEvent() == null) {
            setResult(RESULT_CANCELED);
        } else {
            setResult(RESULT_OK);
        }
        finish();

    }
}
