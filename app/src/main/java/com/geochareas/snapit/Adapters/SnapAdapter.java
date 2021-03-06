package com.geochareas.snapit.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.geochareas.snapit.FeedActivity;
import com.geochareas.snapit.R;
import com.geochareas.snapit.Utility.Snap;
import com.geochareas.snapit.ViewProfile;
import com.geochareas.snapit.WatchSnap;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class SnapAdapter extends RecyclerView.Adapter<SnapAdapter.ViewHolder> {
    private ArrayList<Snap> mDataset;
    private FeedActivity mActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView mPicture;
        public TextView mUsername;
        public TextView mLocation;
        public TextView mTime;
        public LinearLayout mSnapRow;

        public ViewHolder(View v) {
            super(v);
            mPicture = v.findViewById(R.id.profile_photo);
            mUsername = v.findViewById(R.id.username);
            mTime = v.findViewById(R.id.date);
            mLocation = v.findViewById(R.id.location);
            mSnapRow = v.findViewById(R.id.snap_row);
        }
    }

    public SnapAdapter(ArrayList<Snap> myDataset, FeedActivity activity) {
        mDataset = myDataset;
        mActivity = activity;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public SnapAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.snap, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ShimmerFrameLayout container = (ShimmerFrameLayout) mActivity.findViewById(R.id.shimmer_post_placeholder);

        container.stopShimmer();
        container.setVisibility(View.GONE);

        final ViewHolder mHolder = holder;
        final Snap snap = (Snap) mDataset.get(position);

        if (snap.user != null) {


            Picasso.get().load(snap.user.photoUrl).into(holder.mPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    mHolder.mUsername.setText(snap.user.username);
                    mHolder.mLocation.setText(snap.location);
                    mHolder.mTime.setText(dateToText(snap.date));
                }

                @Override
                public void onError(Exception e) {

                }

            });
        }

        holder.mSnapRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent i = new Intent(v.getContext(), WatchSnap.class);
                i.putExtra("Snap", snap);
                i.putExtra("downloadUrl", snap.downloadUrl);
                i.putExtra("fullname", snap.user.fullname);
                i.putExtra("photoUrl", snap.user.photoUrl);
                i.putExtra("snapId", snap.key);
                i.putExtra("location",snap.location);
                v.getContext().startActivity(i);
                return true;
            }
        });

        holder.mUsername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent i = new Intent(v.getContext(), ViewProfile.class);
                i.putExtra("User", snap.user);
                v.getContext().startActivity(i);
                return false;
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addSnap(Snap snap) {
        mDataset.add(0, snap);
        notifyDataSetChanged();
    }


    private String dateToText(String dataDate) { // function to convert Date objects to text

        String convTime = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + "s";
            } else if (minute < 60) {
                convTime = minute + "m";
            } else if (hour < 24) {
                convTime = hour + "h";
            } else if (day >= 7) {
                if (day > 30) {
                    convTime = (day / 30) + "months";
                } else if (day > 360) {
                    convTime = (day / 360) + "years";
                } else {
                    convTime = (day / 7) + "week";
                }
            } else if (day < 7) {
                convTime = day + "d";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeError", e.getMessage());
        }

        return convTime;

    }

}

