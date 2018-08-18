package com.alaa.ahmed.contactssaver;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.holder> {


    private List<Contact> contacts;
    private List<Integer> selectedIds = new ArrayList<>();

    ContactsListAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void add(Contact contact) {
        contacts.add(contact);
        notifyDataSetChanged();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacr_item, parent, false);
        return new holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.mContactName.setText(contacts.get(position).name);
        holder.mContactNumber.setText(contacts.get(position).phoneNumber);
        holder.mContactAddress.setText(contacts.get(position).address);
        if (selectedIds.contains(contacts.get(position).id)) {
            //if item is selected then,set foreground color of FrameLayout.
            holder.itemView.setBackground(new ColorDrawable(ContextCompat.getColor(holder.mCall.getContext(), R.color.colorControlActivated)));
        } else {
            //else remove selected item color.
            holder.itemView.setBackground(new ColorDrawable(ContextCompat.getColor(holder.mCall.getContext(), android.R.color.transparent)));
        }

    }

    public Contact getItem(int position) {
        return contacts.get(position);
    }

    public void setSelectedIds(List<Integer> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class holder extends RecyclerView.ViewHolder {

        private TextView mContactName;
        private TextView mContactNumber;
        private TextView mContactAddress;

        private LinearLayout mCall;
        private LinearLayout mEdit;
        private LinearLayout mDelete;

        holder(View itemView) {
            super(itemView);
            mContactName = itemView.findViewById(R.id.contact_name);
            mContactNumber = itemView.findViewById(R.id.contact_number);
            mContactAddress = itemView.findViewById(R.id.contact_address);
            mCall = itemView.findViewById(R.id.call);
            mEdit = itemView.findViewById(R.id.edit);
            mDelete = itemView.findViewById(R.id.delete);
            mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contacts.get(getAdapterPosition()).phoneNumber));
//                    ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{Manifest.permission.CALL_PHONE}, 6);
//
//                    if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//                    v.getContext().startActivity(intent);
                    String formattedNumber = contacts.get(getAdapterPosition()).phoneNumber;
                    formattedNumber = formattedNumber.replace("+", "").replace(" ", "");
                    try {
                        Intent sendIntent = new Intent("android.intent.action.MAIN");
                        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
                        sendIntent.putExtra("jid", formattedNumber + "@s.whatsapp.net");
                        sendIntent.setPackage("com.whatsapp");
                        v.getContext().startActivity(sendIntent);
                    } catch (Exception e) {
                        Toast.makeText(v.getContext(), "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DialogAc.class);
                    intent.putExtra("name", contacts.get(getAdapterPosition()).name);

                    intent.putExtra("phone", contacts.get(getAdapterPosition()).phoneNumber);
                    intent.putExtra("id", contacts.get(getAdapterPosition()).id);
                    intent.putExtra("address", contacts.get(getAdapterPosition()).address);

                    v.getContext().startActivity(intent);
                }
            });

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                            .setMessage("Are your Sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Contact contact = contacts.get(getAdapterPosition());
                                    contact.delete();
                                    contacts.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    alertDialog.show();
                }
            });

        }
    }

}
