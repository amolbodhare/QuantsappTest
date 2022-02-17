package com.example.quantsapptest.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.quantsapptest.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Locale;

public class ApiListAdapter extends RecyclerView.Adapter<ApiListAdapter.MyViewHolder>
{
    private Context context;
    private JSONArray jsonArray;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewName;
        TextView textViewStatusTv;
        TextView textViewTimestampTv;
        ImageView proPicImv;
        ImageView backPicImv;



        public MyViewHolder(View itemView) {
            super(itemView);

            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.textViewName = (TextView) itemView.findViewById(R.id.nameTv);
            this.textViewStatusTv = (TextView) itemView.findViewById(R.id.statusTv);
            this.textViewTimestampTv = (TextView) itemView.findViewById(R.id.timestampTv);
            this.proPicImv = (ImageView) itemView.findViewById(R.id.proPic);
            this.backPicImv = (ImageView) itemView.findViewById(R.id.backImg);

        }
    }

    public ApiListAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray=jsonArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.api_item_layout, parent, false);
        //view.setOnClickListener(EventListFragment.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int listPosition) {
        CardView cardViewForClick = holder.cardView;
        TextView textViewName = holder.textViewName;
        TextView textViewStatusTv = holder.textViewStatusTv;
        TextView textViewTimestampTv = holder.textViewTimestampTv;
        ImageView proPicImv = holder.proPicImv;
        ImageView backPicImv = holder.backPicImv;

        Log.v("ListPosition", String.valueOf(listPosition));


        try
        {
            textViewName.setText(jsonArray.getJSONObject(listPosition).getString("name"));
            textViewStatusTv.setText(jsonArray.getJSONObject(listPosition).getString("status"));

            //textViewCompName.setText(jsonArray.getJSONObject(listPosition).getString("timeStamp"));
            textViewTimestampTv.setText(getDate(Long.parseLong(jsonArray.getJSONObject(listPosition).getString("timeStamp"))));

            Log.v("propic",jsonArray.getJSONObject(listPosition).getString("profilePic"));
            //Toast.makeText(context, jsonArray.getJSONObject(listPosition).getString("profilePic"), Toast.LENGTH_LONG).show();
            Picasso.get().load(jsonArray.getJSONObject(listPosition).getString("profilePic")).into(proPicImv);
            Picasso.get().load(jsonArray.getJSONObject(listPosition).getString("image")).into(backPicImv);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        cardViewForClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Toast.makeText(context, jsonArray.getJSONObject(listPosition).getString("id")+" : "+jsonArray.getJSONObject(listPosition).getString("name"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

}
