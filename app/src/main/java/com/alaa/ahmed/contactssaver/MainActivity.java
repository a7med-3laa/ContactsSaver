package com.alaa.ahmed.contactssaver;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActionMode.Callback {

    HashMap<String, String> map;
    ContactsListAdapter contactsListAdapter;
    List<Contact> organizationList;
    Toolbar toolbar;
    private RecyclerView mContactList;
    private android.view.ActionMode actionMode;
    private boolean isMultiSelect = false;
    //i created List of int type to store id of data, you can create custom class type data according to your need.
    private List<Integer> selectedIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // don't overwrite an existing job with
        FloatingActionButton fab = findViewById(R.id.fab);
        ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS}, 6);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, DialogAc.class);
                startActivity(intent);
            }
        });
        mContactList = findViewById(R.id.contactList);
        List<Contact> organizationList = SQLite.select().
                from(Contact.class).queryList();
        mContactList.setLayoutManager(new LinearLayoutManager(this));
        mContactList.setAdapter(new ContactsListAdapter(organizationList));
        map = new HashMap<String, String>();
        for (Contact contact : organizationList) {
            map.put(contact.phoneNumber, contact.name);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            hasWhatsapp(map);

        }
        mContactList.addOnItemTouchListener(new RecyclerItemClickListener(this, mContactList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    //if multiple selection is enabled then select item on single click else perform normal click on item.
                    multiSelect(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null) {
                        actionMode = toolbar.startActionMode(MainActivity.this); //show ActionMode.
                    }
                }

                multiSelect(position);
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        organizationList = SQLite.select().
                from(Contact.class).orderBy(OrderBy.fromProperty(Contact_Table.name).ascending()).queryList();
        mContactList.setLayoutManager(new LinearLayoutManager(this));
        contactsListAdapter = new ContactsListAdapter(organizationList);
        mContactList.setAdapter(contactsListAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void GetContactsIntoArrayList(HashMap<String, String> contacts) {

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //            String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.Add));
                if (!contacts.containsKey(phonenumber)) {
                    Contact contact = new Contact();
                    contact.phoneNumber = phonenumber;
                    contact.name = name;
                    contact.save();
                    contacts.put(phonenumber, name);
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }

    }

    public boolean hasWhatsapp(HashMap<String, String> map) {
        ContentResolver cr = getContentResolver();

//RowContacts for filter Account Types
        Cursor contactCursor = cr.query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID,
                        ContactsContract.RawContacts.CONTACT_ID},
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[]{"com.whatsapp"},
                null);

//ArrayList for Store Whatsapp Contact
        ArrayList<String> myWhatsappContacts = new ArrayList<>();

        if (contactCursor != null) {
            if (contactCursor.getCount() > 0) {
                if (contactCursor.moveToFirst()) {
                    do {
                        //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                        String whatsappContactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));

                        if (whatsappContactId != null) {
                            //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID
                            Cursor whatsAppContactCursor = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{whatsappContactId}, null);

                            if (whatsAppContactCursor != null) {
                                whatsAppContactCursor.moveToFirst();
                                String id = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                                String name = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String number = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (!number.trim().startsWith("+2"))
                                    number = "+2" + number.trim();
                                whatsAppContactCursor.close();

                                //Add Number to ArrayList
                                myWhatsappContacts.add(number);
                                if (!map.containsKey(number)) {
                                    Contact contact = new Contact();

                                    contact.phoneNumber = number;
                                    contact.name = name;
                                    contact.save();
                                    map.put(number, name);
                                }
                            }
                        }
                    } while (contactCursor.moveToNext());
                    contactCursor.close();
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            {
                hasWhatsapp(map);
                organizationList = SQLite.select().
                        from(Contact.class).orderBy(OrderBy.fromProperty(Contact_Table.name)).queryList();
                mContactList.setLayoutManager(new LinearLayoutManager(this));
                contactsListAdapter = new ContactsListAdapter(organizationList);
                mContactList.setAdapter(contactsListAdapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select, menu);
        return true;
    }

    private void multiSelect(int position) {
        Contact data = contactsListAdapter.getItem(position);
        if (data != null) {
            if (actionMode != null) {
                if (selectedIds.contains(data.getId()))
                    selectedIds.remove(Integer.valueOf(data.getId()));
                else
                    selectedIds.add(data.getId());

                if (selectedIds.size() > 0)
                    actionMode.setTitle(String.valueOf(selectedIds.size())); //show selected item count on action mode.
                else {
                    actionMode.setTitle(""); //remove item count from action mode.
                    actionMode.finish(); //hide action mode.
                }
                contactsListAdapter.setSelectedIds(selectedIds);

            }
        }

    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.send:
                //just to show selected items.
                for (Contact data : organizationList) {
                    if (selectedIds.contains(data.getId())) {

                    }

                }
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        isMultiSelect = false;
        selectedIds = new ArrayList<>();
        contactsListAdapter.setSelectedIds(new ArrayList<Integer>());
    }
}
