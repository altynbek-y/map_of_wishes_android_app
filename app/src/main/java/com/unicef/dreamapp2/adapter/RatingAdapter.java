package com.unicef.dreamapp2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unicef.dreamapp2.BaseInterface;
import com.unicef.dreamapp2.R;
import com.unicef.dreamapp2.model.ChannelModel;
import com.unicef.dreamapp2.model.RatingModel;
import com.unicef.dreamapp2.singleclicklistener.OnSingleClickListener;

import java.util.List;

/**
 * @author Iman Augustine
 *
 * MessageAdapter is used as messages adapter for the RecyclerView
 * used to display the chat messages between two users.
 *
 * */

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MessageViewHolder> {

    private String TAG = "RatingAdapter";

    // Global variables
    private List<RatingModel> ratingsList;
    private BaseInterface.OnItemClickListener onItemClickListener;
    private Context context;

    // Adapter constructor
    public RatingAdapter(List<RatingModel> ratingsList, Context context,
                         BaseInterface.OnItemClickListener onItemClickListener) {
        this.ratingsList = ratingsList; // Channels list
        this.context = context; // Context
        this.onItemClickListener = onItemClickListener;
    }

    // Sets values
    public void setValues(List<RatingModel> list) {
         this.ratingsList.clear();
         if(list!=null) { // If the list is not empty
             this.ratingsList.addAll(list); // Adding all message lists
         }
         this.notifyDataSetChanged(); // Notify data set change
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.volunteer_rating_item, parent, false);

        return new MessageViewHolder(itemView, onItemClickListener);
    }

    // On bind view holder
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        try {
            holder.name.setText(ratingsList.get(position).getVolunteerName()); // Set volunteer name
            holder.likesNumber.setText((""+ratingsList.get(position).getLikes())); // Set number of likes
        } catch(Exception error) {
            Log.d(TAG, "onBindViewHolder, error: "+error.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return ratingsList != null ? ratingsList.size() : 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        // Global variables
        private TextView name; // Name
        private TextView likesNumber; // Likes number

        // Message view holder
        private MessageViewHolder(View view, final BaseInterface.OnItemClickListener onItemClickListener) {
            super(view);

            // Views initialize
            name = view.findViewById(R.id.name);
            likesNumber = view.findViewById(R.id.likesNumberText);
        }
    }
}
