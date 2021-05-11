package com.mtha.findmyfriends.data.model;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mtha.findmyfriends.R;

import java.util.ArrayList;
import java.util.List;

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
        convertView.findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "call ....", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}
