package com.mtha.findmyfriends.data.model;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mtha.findmyfriends.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {
    Context context;
    List<Contact> listContact;
    List<Contact> listContactFilter;
    private ContactAdapterListener listener;

    public ContactAdapter(Context context, List<Contact> listContact, ContactAdapterListener listener) {
        this.listener = listener;
        this.context = context;
        this.listContact = listContact;
        this.listContactFilter = listContact;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = listContact.get(position);
        holder.tvFullName.setText(contact.getFullName());
    }

    @Override
    public int getItemCount() {
        return listContactFilter.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listContactFilter = listContact;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : listContact) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getFullName().toLowerCase().contains(charString.toLowerCase()) || row.getPhoneNumb().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    listContactFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listContactFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listContactFilter = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvFullName;
        ImageButton btnInfo, btnCall, btnChat;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnChat = itemView.findViewById(R.id.btnChat);
            img = itemView.findViewById(R.id.imgPerson);
            //xu ly su kien o day
            btnChat.setOnClickListener(this);
            btnInfo.setOnClickListener(this);
            btnCall.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //send select contact in callback
                    listener.onContactSelected(listContactFilter.get(getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnInfo:
                    Toast.makeText(context,"Contact info",Toast.LENGTH_LONG).show();
                    break;
                case R.id.btnCall:
                    break;
                case R.id.btnChat:
                    Toast.makeText(context,"Chat Info", Toast.LENGTH_LONG).show();
                    break;
            }

        }
    }

    public interface ContactAdapterListener {
        void onContactSelected(Contact contact);
    }
}
