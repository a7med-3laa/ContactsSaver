package com.alaa.ahmed.contactssaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        Toast.makeText(context, "Run  " + intent.getStringExtra(TelephonyManager.EXTRA_STATE) + " -- " + TelephonyManager.CALL_STATE_RINGING, Toast.LENGTH_SHORT).show();
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Toast.makeText(context, "inside", Toast.LENGTH_SHORT).show();
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                List<Contact> organizationList = SQLite.select().
                        from(Contact.class).where(Contact_Table.phoneNumber.eq(incomingNumber)).queryList();
                if (organizationList.size() > 0) {
                    Intent intent2 = new Intent(context, DialogCall.class);
                    intent2.putExtra("name", organizationList.get(0).name);

                    intent2.putExtra("phone", incomingNumber);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                } else {
                    Intent intent2 = new Intent(context, DialogAc.class);
                    intent2.putExtra("phone", incomingNumber);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
