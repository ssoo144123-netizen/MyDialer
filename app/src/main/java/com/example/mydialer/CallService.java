package com.example.mydialer;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        String number = numberOf(call);

        // اگر شماره بلاک است، تماس ورودی را بدون نمایش رد کن
        if (call.getState() == Call.STATE_RINGING && BlockList.isBlocked(this, number)) {
            call.reject(false, null);
            return;
        }

        InCallActivity.activeCall = call;
        Intent i = new Intent(this, InCallActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onCallRemoved(Call call) {
        if (InCallActivity.activeCall == call) {
            InCallActivity.activeCall = null;
        }
    }

    static String numberOf(Call call) {
        Call.Details d = call.getDetails();
        if (d == null || d.getHandle() == null) return "";
        return d.getHandle().getSchemeSpecificPart();
    }
}
