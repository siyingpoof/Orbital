package com.example.alvinaong.alive;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SignUpsAdapter extends RecyclerView.Adapter<SignUpsAdapter.ViewHolder> {

    private Context mContext;
    private List<Volunteer> mData;

    public SignUpsAdapter(List<Volunteer> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public SignUpsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        mContext = parent.getContext();
        v = LayoutInflater.from(mContext).inflate(R.layout.sign_ups_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SignUpsAdapter.ViewHolder holder, int position) {
        final Volunteer currVol = mData.get(position);
        holder.volName.setText(currVol.getName());
        holder.volNumber.setText(currVol.getContactNumber());
        holder.volEmail.setText(currVol.getEmail());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView volName, volNumber, volEmail;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            volName = (TextView) itemView.findViewById(R.id.signupname);
            volNumber = (TextView) itemView.findViewById(R.id.signupmobile);
            volEmail = (TextView) itemView.findViewById(R.id.signupemail);
            cardView = (CardView) itemView.findViewById(R.id.sign_ups_cardview);
        }
    }
}
