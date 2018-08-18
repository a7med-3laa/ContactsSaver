package com.alaa.ahmed.contactssaver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogCall extends AppCompatActivity {

    private TextView mContactName;
    private TextView mContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_call);
        setWindowParams();
        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");

        mContactName = findViewById(R.id.contact_name);
        mContactNumber = findViewById(R.id.contact_number);

        mContactName.setText(name);
        mContactNumber.setText(phone);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    private void setWindowParams() {
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.dimAmount = 0;
//        wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(wlp);
        setFinishOnTouchOutside(true);

    }
}
