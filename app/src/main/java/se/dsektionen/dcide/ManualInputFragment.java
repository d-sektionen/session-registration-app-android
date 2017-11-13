package se.dsektionen.dcide;

import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Gustav on 2017-11-13.
 */

public class ManualInputFragment extends Fragment implements OnClickListener{

    Button doneButton;
    EditText adminTokenInput;
    EditText sessionIdInput;
    TextView warningTV;

    String adminToken;
    String sessionID;

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

        adminTokenInput = view.findViewById(R.id.admin_token_input);
        sessionIdInput = view.findViewById(R.id.session_id_input);

        warningTV = view.findViewById(R.id.warning_no_input_textview);
        return view;
    }

    @Override
    public void onClick(View view) {
        sessionID = sessionIdInput.getText().toString();
        adminToken = adminTokenInput.getText().toString();

        if(view.getId() == doneButton.getId()){
            if(sessionID.length()>0 && adminToken.length()>0){
                ((NewSessionActivity) getActivity()).onSessionInfoComplete(sessionID,adminToken);
            } else {
                warningTV.setVisibility(View.VISIBLE);
            }
        }
    }


}
