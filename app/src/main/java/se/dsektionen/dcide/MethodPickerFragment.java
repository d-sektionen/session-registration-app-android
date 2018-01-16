package se.dsektionen.dcide;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.security.KeyStore;

/**
 * Created by Gustav on 2017-11-13.
 */

public class MethodPickerFragment extends Fragment implements View.OnClickListener{

    private View manualView;
    private View QRView;
    private TextView QRFail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_method_picker_dialog,container,false);
        manualView = view.findViewById(R.id.input_manually_view);
        QRView = view.findViewById(R.id.qr_option_view);
        QRFail = view.findViewById(R.id.warning_qr_fail);

        manualView.setOnClickListener(this);
        QRView.setOnClickListener(this);

        return view;
    }

    public void showQRFailedView(){
        QRFail.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == manualView.getId()){
            ((NewSessionActivity) getActivity()).onMethodManual();
        } else if (view.getId() == QRView.getId()){
            ((NewSessionActivity) getActivity()).onMethodQR();
        }
    }

}
