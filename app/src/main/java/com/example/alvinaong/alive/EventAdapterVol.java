package com.example.alvinaong.alive;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import com.bumptech.glide.Glide;

import java.util.List;

class EventAdapterVol extends RecyclerView.Adapter<EventAdapterVol.ViewHolder> {

    private List<Event> mData;
    private Context mContext;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    public EventAdapterVol(List<Event> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public EventAdapterVol.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        mContext = parent.getContext();
        v = LayoutInflater.from(mContext).inflate(R.layout.cardview_item_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventAdapterVol.ViewHolder holder, int position) {
        final Event event = mData.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDate());
        holder.eventTime.setText(event.getTime());
        holder.eventQuota.setText(Integer.toString(event.getQuota()));

        databaseReference = FirebaseDatabase.getInstance().getReference("organisers").child(event.getOrganiser());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Organiser organiser = dataSnapshot.getValue(Organiser.class);
                holder.organiserName.setText(organiser.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("events").child(event.getEventID()+".jpg");
        Glide.with(mContext).load(storageReference).into(holder.eventImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Event_View_Vol.class);
                intent.putExtra("eventID", event.getEventID());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView eventDate;
        TextView eventTime;
        TextView eventQuota;
        TextView organiserName;
        ImageView eventImage;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.event_name_id);
            eventDate = (TextView) itemView.findViewById(R.id.event_date_id);
            eventTime = (TextView) itemView.findViewById(R.id.event_time_id);
            eventQuota = (TextView) itemView.findViewById(R.id.event_quota_id);
            organiserName = (TextView) itemView.findViewById(R.id.event_organiser_id);
            eventImage = (ImageView) itemView.findViewById(R.id.org_logo_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}