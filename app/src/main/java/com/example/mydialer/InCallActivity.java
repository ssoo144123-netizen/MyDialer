package com.example.mydialer;

import android.app.Activity;
import android.os.Bundle;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class InCallActivity extends Activity {

    static Call activeCall;

    private Call call;
    private TextView numberView, stateView;
    private Button acceptBtn, rejectBtn, blockBtn;

    private final Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call c, int state) {
            if (state == Call.STATE_DISCONNECTED) {
                finish();
            } else {
                updateUi();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_incall);

        call = activeCall;
        if (call == null) {
            finish();
            return;
        }

        stateView = (TextView) findViewById(R.id.state);
        numberView = (TextView) findViewById(R.id.number);
        acceptBtn = (Button) findViewById(R.id.accept);
        rejectBtn = (Button) findViewById(R.id.reject);
        blockBtn = (Button) findViewById(R.id.block);

        String number = CallService.numberOf(call);
        numberView.setText(number.isEmpty() ? "ناشناس" : number);

        call.registerCallback(callback);

        acceptBtn.setOnClickListener(v -> call.answer(VideoProfile.STATE_AUDIO_ONLY));
        rejectBtn.setOnClickListener(v -> endCall());
        blockBtn.setOnClickListener(v -> {
            BlockList.add(this, CallService.numberOf(call));
            endCall();
        });

        updateUi();
    }

    private void endCall() {
        if (call.getState() == Call.STATE_RINGING) {
            call.reject(false, null);
        } else {
            call.disconnect();
        }
    }

    private void updateUi() {
        boolean ringing = call.getState() == Call.STATE_RINGING;
        stateView.setText(ringing ? "تماس ورودی" : "در حال مکالمه");
        acceptBtn.setVisibility(ringing ? View.VISIBLE : View.GONE);
        blockBtn.setVisibility(ringing ? View.VISIBLE : View.GONE);
        rejectBtn.setText(ringing ? "رد" : "پایان");
    }

    @Override
    protected void onDestroy() {
        if (call != null) call.unregisterCallback(callback);
        super.onDestroy();
    }
}
