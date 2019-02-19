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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.Glide;

import java.util.List;

class EventAdapterVolLog extends RecyclerView.Adapter<EventAdapterVolLog.ViewHolder> {

    private List<Event> mData;
    private Context mContext;
    private StorageReference storageReference;

    public EventAdapterVolLog(List<Event> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public EventAdapterVolLog.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        mContext = parent.getContext();
        v = LayoutInflater.from(mContext).inflate(R.layout.events_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapterVolLog.ViewHolder holder, int position) {
        final Event event = mData.get(position);
        holder.eventName.setText(event.getName());

        storageReference = FirebaseStorage.getInstance().getReference("events").child(event.getEventID()+".jpg");
        Glide.with(mContext).load(storageReference).into(holder.eventImage);

        holder.cardview.setOnClickListener(new View.OnClickListener() {
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
        ImageView eventImage;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.tvEventName);
            eventImage = (ImageView) itemView.findViewById(R.id.ivEventImage);
            cardview = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}