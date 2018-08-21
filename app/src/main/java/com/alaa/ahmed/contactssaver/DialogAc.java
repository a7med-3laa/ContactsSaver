package com.alaa.ahmed.contactssaver;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class DialogAc extends AppCompatActivity {

    private TextInputEditText mContactName;
    private TextInputEditText mContactNumber;
    private TextInputEditText mContactAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setWindowParams();
        setFinishOnTouchOutside(true);
        mContactName = findViewById(R.id.contact_name);
        mContactNumber = findViewById(R.id.contact_number);
        mContactAddress = findViewById(R.id.contact_address);

        Button mButton = findViewById(R.id.button);


        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        String address = getIntent().getStringExtra("address");
        final int id = getIntent().getIntExtra("id", -1);

        if (name == null) {
            name = "";
        }
        if (phone == null) {
            phone = "";
        }
        if (address == null) {
            address = "";
        }
        mContactName.setText(name);
        mContactNumber.setText(phone);
        mContactAddress.setText(address);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String finalPhone = mContactNumber.getText().toString();
                final String finalName = mContactName.getText().toString();
                final String finalAddress = mContactAddress.getText().toString();

                if (TextUtils.isEmpty(finalName)) {
                    mContactName.setError("Name is Required");
                    return;
                }

                if (TextUtils.isEmpty(finalPhone)) {
                    mContactNumber.setError("Phone number is Required");
                    return;
                }

                Contact contact = new Contact();
                if (id != -1) {
                    contact.id = id;
                }
                contact.name = finalName;
                contact.phoneNumber = finalPhone;
                contact.address = finalAddress;
                contact.save();
                finish();
            }
        });
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
        wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(wlp);
    }

    public void close(View view) {
        finish();
    }
}
