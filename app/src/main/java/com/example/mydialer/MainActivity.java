package com.example.mydialer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int REQ_CALL = 1;
    private static final String DEL = "حذف";
    private static final String CALL = "تماس";

    private TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(12);
        root.setPadding(pad, pad, pad, pad);

        display = new TextView(this);
        display.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        display.setGravity(Gravity.CENTER);
        root.addView(display, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        String[][] keys = {
                {"1", "2", "3"},
                {"4", "5", "6"},
                {"7", "8", "9"},
                {"*", "0", "#"},
                {DEL, CALL}
        };

        for (String[] row : keys) {
            LinearLayout r = new LinearLayout(this);
            for (String k : row) {
                Button btn = new Button(this);
                btn.setText(k);
                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                btn.setOnClickListener(v -> onKey(k));
                r.addView(btn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            }
            root.addView(r, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        }

        Button defaultBtn = new Button(this);
        defaultBtn.setText("تنظیم به‌عنوان تماس‌گیرنده پیش‌فرض");
        defaultBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        defaultBtn.setOnClickListener(v -> {
            Intent i = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            i.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivity(i);
        });
        root.addView(defaultBtn, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(root);
    }

    private void onKey(String k) {
        if (DEL.equals(k)) {
            String s = display.getText().toString();
            if (!s.isEmpty()) display.setText(s.substring(0, s.length() - 1));
        } else if (CALL.equals(k)) {
            placeCall();
        } else {
            display.append(k);
        }
    }

    private void placeCall() {
        String num = display.getText().toString();
        if (num.isEmpty()) return;

        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQ_CALL);
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CALL && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            placeCall();
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
