package se.dsektionen.dcide;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Gustav on 2017-11-13.
 */

public class ManualInputFragment extends Fragment implements OnClickListener{

    private Button doneButton;
    private EditText adminTokenInput;
    private EditText sessionIdInput;
    private TextView warningTV;

    private ArrayList<View> views = new ArrayList<>();

    private String adminToken;
    private String sessionID;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manual_input,container,false);
        doneButton = view.findViewById(R.id.input_done_button);
        doneButton.setOnClickListener(this);
        views.add(doneButton);

        progressBar = view.findViewById(R.id.progressbar);

        adminTokenInput = view.findViewById(R.id.admin_token_input);
        sessionIdInput = view.findViewById(R.id.session_id_input);
        views.add(adminTokenInput);
        views.add(sessionIdInput);

        warningTV = view.findViewById(R.id.warning_textview);
        return view;
    }

    private void showError(boolean showError, String error){
        if(showError){
            warningTV.setText(error);
            warningTV.setVisibility(View.VISIBLE);
        } else {
            warningTV.setVisibility(View.GONE);
        }
    }


    private void setViewsEnabled(boolean enabled){
        for (View v : views){
            v.setEnabled(enabled);
        }
    }

    private void setLoading(boolean loading){
        showError(false,"");
        if(loading) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);

        setViewsEnabled(!loading);
    }



    @Override
    public void onClick(View view) {
        sessionID = sessionIdInput.getText().toString();
        adminToken = adminTokenInput.getText().toString();

        if(view.getId() == doneButton.getId()){
            if(sessionID.length()>0 && adminToken.length()>0){
                setLoading(true);
                RequestUtils.validateSession(sessionID, new ResultHandler() {
                    @Override
                    public void onResult(String response, int status) {
                        if(status == RequestUtils.STATUS_OK){
                            ((NewSessionActivity) getActivity()).onSessionInfoComplete(sessionID,adminToken,response);
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    setLoading(false);
                                    showError(true,"Inte en giltig session. Försök igen.");
                                }
                            });
                        }
                    }
                });
                //
            } else {
                showError(true,"Båda fälten måste vara ifyllda.");
            }
        }
    }


}
