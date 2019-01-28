package se.dsektionen.dcide.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

import se.dsektionen.dcide.DCideApp;
import se.dsektionen.dcide.JsonModels.Event;
import se.dsektionen.dcide.JsonModels.Meeting;
import se.dsektionen.dcide.JsonModels.Participant;
import se.dsektionen.dcide.R;
import se.dsektionen.dcide.Requests.Callbacks.AddAttendantCallback;
import se.dsektionen.dcide.Requests.Callbacks.AddParticipantCallback;
import se.dsektionen.dcide.Requests.Callbacks.RemoveAttendantCallback;
import se.dsektionen.dcide.Requests.Callbacks.RemoveParticipantCallback;
import se.dsektionen.dcide.Requests.DownloadImageTask;
import se.dsektionen.dcide.Utilities.EventManager;
import se.dsektionen.dcide.Utilities.MeetingManager;
import se.dsektionen.dcide.Utilities.NFCForegroundUtil;

/**
 * Created by gustavaaro on 2016-11-29.
 */


public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private final int MEETING_REQUEST = 3;
    NFCForegroundUtil nfcForegroundUtil = null;
    private TextView currentSessionTV;
    private TextView nfcWarningTV;
    private EditText idField;
    private Button registerButton;
    private ImageView sectionIcon;
    private NfcAdapter mNfcAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Build.VERSION.SDK_INT >= 18 && action != null) {
                if (action.equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
                    updateNFCView();
                }
            }
        }
    };
    private Meeting currentMeeting;
    private Event currentEvent;
    private CoordinatorLayout coordinatorLayout;
    private ScrollView scrollView;
    private TextInputLayout idView;
    private MeetingManager meetingManager;
    private EventManager eventManager;
    private boolean isInRegistrationMode = true;

    // Konverterar bytes till hexadecimal
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    // Används för att då fram rätt kort-id:n som ligger lagrade som en bakvänd hex-sträng
    static String bin2int(byte[] data) {
        byte[] reverse = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            reverse[data.length - i - 1] = data[i];
        }

        return Long.toString(Long.valueOf(bytesToHex(reverse), 16));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcForegroundUtil = new NFCForegroundUtil(this);
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
        sectionIcon = findViewById(R.id.sectionIcon);
        idField = findViewById(R.id.id_field);
        currentSessionTV = findViewById(R.id.currentSessionTV);
        nfcWarningTV = findViewById(R.id.nfc_warning_view);
        coordinatorLayout = findViewById(R.id.snackbarPosition);
        scrollView = findViewById(R.id.scrollView);
        idView = findViewById(R.id.id_field_view);

        idField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerButton.callOnClick();
                    return true;
                }
                return false;
            }
        });


        meetingManager = DCideApp.getInstance().getMeetingManager();
        eventManager = DCideApp.getInstance().getEventManager();

        RadioGroup actionGroup = findViewById(R.id.action_group);
        actionGroup.setOnCheckedChangeListener(this);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        chooseMeeting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcForegroundUtil != null && mNfcAdapter != null) {
            nfcForegroundUtil.disableForeground();
        }
        this.unregisterReceiver(mReceiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Visar status-meddelande en kort stund
    private void showResult(String message, boolean success) {

        Snackbar snackbar;

        if (success) {
            SpannableStringBuilder snackBarText = new SpannableStringBuilder();
            snackBarText.append(message);
            snackBarText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.resultOK)), 0, snackBarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snackBarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, snackBarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snackbar = Snackbar.make(coordinatorLayout, snackBarText, Snackbar.LENGTH_LONG);
        } else {
            SpannableStringBuilder snackBarText = new SpannableStringBuilder();
            snackBarText.append(message);
            snackBarText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.resultFail)), 0, snackBarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snackBarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, snackBarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snackbar = Snackbar.make(coordinatorLayout, snackBarText, Snackbar.LENGTH_LONG);
        }

        try {
            Field mAccessibilityManagerField = BaseTransientBottomBar.class.getDeclaredField("mAccessibilityManager");
            mAccessibilityManagerField.setAccessible(true);
            AccessibilityManager accessibilityManager = (AccessibilityManager) mAccessibilityManagerField.get(snackbar);
            Field mIsEnabledField = AccessibilityManager.class.getDeclaredField("mIsEnabled");
            mIsEnabledField.setAccessible(true);
            mIsEnabledField.setBoolean(accessibilityManager, false);
            mAccessibilityManagerField.set(snackbar, accessibilityManager);
        } catch (Exception e) {
            Log.d("Snackbar", "Reflection error: " + e.toString());
        }
        snackbar.show();

    }

    private void showParticipantInfo(Participant participant) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_participant_info);
        dialog.show();
        TextView participantInfo = dialog.findViewById(R.id.participant_info_view);
        participantInfo.setText("");
        participantInfo.append("LiU-id: " + participant.getUsername() + "\n");
        participantInfo.append("Namn: " + participant.getFirst_name() + " " +
                participant.getLast_name() + "\n");
        participantInfo.append("Diet: " + participant.getSpecial_diet() + "\n");
        participantInfo.append("Dryckes preferens: " + participant.getDrink_preference() + "\n");
        participantInfo.append("Gruppnamn: " + participant.getGroup_name() + "\n");

        try {
            JSONObject otherInfo = new JSONObject(participant.getOther_info());
            Iterator<String> keys = otherInfo.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                participantInfo.append(key + ": " + otherInfo.get(key) + "\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        idField.clearFocus();
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
        if (nfcForegroundUtil != null && mNfcAdapter != null) {
            nfcForegroundUtil.enableForeground();
        }
        currentMeeting = DCideApp.getInstance().getMeetingManager().getMeeting();
        currentEvent = DCideApp.getInstance().getEventManager().getEvent();

        if (currentMeeting != null) {
            currentSessionTV.setText(currentMeeting.getName() + " för " + currentMeeting.getSection().getName());
            DownloadImageTask imageTask = new DownloadImageTask(sectionIcon);
            imageTask.execute("https://d-sektionen.se/downloads/logos/" + currentMeeting.getSection().getName().substring(0, 1).toLowerCase() + "-sek_logo.png");
        }

        updateNFCView();
    }


    private void updateNFCView() {
        if (mNfcAdapter != null) {
            if (!nfcForegroundUtil.getNfc().isEnabled()) {
                nfcWarningTV.setVisibility(View.VISIBLE);
            } else {
                nfcWarningTV.setVisibility(View.GONE);
            }
        }
    }


    @SuppressLint("RestrictedApi")
    private void chooseMeeting() {
        Intent newSessionIntent = new Intent(this, ChooseMeetingActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle();
        startActivityForResult(newSessionIntent, MEETING_REQUEST, bundle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        chooseMeeting();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Avsluta");
        builder.setMessage("Vill du verkligen avsluta?");
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Nej", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEETING_REQUEST && resultCode == RESULT_CANCELED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Avsluta");
            builder.setCancelable(false);
            builder.setMessage("Vill du verkligen avsluta?");
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chooseMeeting();
                }
            });
            builder.show();
        }
    }

    private boolean validateId(String id) {
        String regex = "[A-za-z]{5}[0-9]{3}";
        return idField.getText().toString().matches(regex);
    }

    @Override
    public void onClick(View v) {
        if (validateId(idField.getText().toString())) {
            idView.setError(null);
            idView.setErrorEnabled(false);
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

            if(currentMeeting != null) {
                if (isInRegistrationMode) {
                    meetingManager.addAttendantWithId(idField.getText().toString().toLowerCase(), new AddAttendantCallback() {
                        @Override
                        public void onAttendantAdded(String response) {
                            showResult("Deltagare " + response + " lades till", true);
                        }

                        @Override
                        public void addAttendantFailed(String error) {
                            showResult(error, false);

                        }
                    });
                } else {
                    meetingManager.removeAttendantWithId(idField.getText().toString().toLowerCase(), new RemoveAttendantCallback() {
                        @Override
                        public void onRemoveAttendant() {
                            showResult("Deltagare borttagen", true);
                        }

                        @Override
                        public void removeAttendantFailed(String error) {
                            showResult(error, false);
                        }
                    });
                }
            } if(currentEvent != null) {
                if (isInRegistrationMode) {
                    eventManager.addParticipantWithId(idField.getText().toString().toLowerCase(), new AddParticipantCallback() {

                        @Override
                        public void onParticipantAdded(Participant participant) {
                            showParticipantInfo(participant);
                        }

                        @Override
                        public void addParticipantFailed(String error) {
                            showResult(error, false);

                        }
                    });
                } else {
                    eventManager.removeParticipantWithId(idField.getText().toString().toLowerCase(), new RemoveParticipantCallback(){
                        @Override
                        public void onRemoveParticipant() {
                            showResult("Deltagare borttagen", true);
                        }

                        @Override
                        public void removeParticipantFailed(String error) {
                            showResult(error, false);
                        }});
                }
            }

        } else {
            idView.setErrorEnabled(true);
            idView.setError("Inte ett giltigt Liu-ID");
            scrollView.scrollTo(0, idView.getBottom());
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) && currentMeeting != null) {
            getTagInfo(intent);
        } else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) && currentEvent != null){
            try {
                getEventTagInfo(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getEventTagInfo(Intent intent) throws JSONException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(30);

        String rfid = bin2int(tag.getId());
        if (isInRegistrationMode) {
            eventManager.addParticipantWithRfid(rfid, new AddParticipantCallback() {

                @Override
                public void onParticipantAdded(Participant participant) {
                    showParticipantInfo(participant);
                }

                @Override
                public void addParticipantFailed(String error) {
                    showResult(error, false);

                }
            });
        } else {
            eventManager.removeParticipantwithRfid(rfid, new RemoveParticipantCallback(){
                @Override
                public void onRemoveParticipant() {
                    showResult("Deltagare borttagen", true);
                }

                @Override
                public void removeParticipantFailed(String error) {
                    showResult(error, false);
                }});
    }}

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(30);

        String rfid = bin2int(tag.getId());
        if (isInRegistrationMode) {
            meetingManager.addAttendantWithRfid(rfid, new AddAttendantCallback() {
                @Override
                public void onAttendantAdded(String response) {
                    showResult(response + " lades till", true);
                }

                @Override
                public void addAttendantFailed(String error) {
                    showResult(error, false);
                }
            });
        } else {
            meetingManager.removeAttendantwithRfid(rfid, new RemoveAttendantCallback() {
                @Override
                public void onRemoveAttendant() {
                    showResult("Deltagare borttagen", true);
                }

                @Override
                public void removeAttendantFailed(String error) {
                    showResult(error, false);
                }
            });
        }

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        isInRegistrationMode = id == R.id.radioAdd;
        if (idField.hasFocus()) {
            idField.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        if (isInRegistrationMode) {
            registerButton.setText("Registrera");
        } else {
            registerButton.setText("Ta bort");
        }
    }

}
