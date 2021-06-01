package com.mtha.findmyfriends.data.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mtha.findmyfriends.R;

import java.util.ArrayList;


public class ContactAdapter extends ArrayAdapter<Contact> {
    Context context;
    ArrayList<Contact> listContact;
    int resource;

    public ContactAdapter(@NonNull Context context, ArrayList<Contact> listContact, int resource) {
        super(context, resource);
        this.context = context;
        this.listContact = listContact;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return listContact.size();
    }

    @Nullable
    @Override
    public Contact getItem(int position) {
        return listContact.get(position);
    }

    @Override
    public int getPosition(@Nullable Contact item) {
        return listContact.indexOf(item);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        convertView=inflater.inflate(resource,null);
        //get view
        TextView tvFullName = convertView.findViewById(R.id.tvFullName);
        tvFullName.setText(listContact.get(position).getFullName());
        ImageView btnCall = convertView.findViewById(R.id.btnCall);
        ImageView btnInfo = convertView.findViewById(R.id.btnInfo);
        ImageView btnChat = convertView.findViewById(R.id.btnChat);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "call ....", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}
