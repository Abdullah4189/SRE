package com.example.sre.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sre.Activity.AdDetails;
import com.example.sre.Domain.AdModel;
import com.example.sre.R;

import java.util.ArrayList;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.Viewholder> {

    ArrayList<AdModel> ads = new ArrayList<>();
    Context context;

    public AdAdapter(ArrayList<AdModel> ads, Context context) {
        this.ads = ads;
        this.context = context;
    }

    @NonNull
    @Override
    public AdAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.ad_card, parent, false);
        Viewholder viewholder = new Viewholder(v);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdAdapter.Viewholder holder, int position) {


//                        Glide.with(context)
//                .load(ads.get(position).img_url)
//                .into(holder.ad_image);

        if(ads.get(position).title.toLowerCase().contains("burger"))
        {
            holder.ad_image.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.burger, null));
        } else if(ads.get(position).title.toLowerCase().contains("daal"))
        {
            holder.ad_image.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.daal, null));
        } else if(ads.get(position).title.toLowerCase().contains("pizza"))
        {
            holder.ad_image.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.pizza, null));
        } else if(ads.get(position).title.toLowerCase().contains("biryani"))
        {
            holder.ad_image.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.biryani, null));
        } else if(ads.get(position).title.toLowerCase().contains("sofa"))
        {
            holder.ad_image.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.sofa, null));
        } else if(ads.get(position).title.toLowerCase().contains("table"))
        {
            holder.ad_image.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.table, null));
        }

        holder.title.setText(ads.get(position).title);
        holder.category.setText(ads.get(position).category);
        if(ads.get(position).type.equals("Sell"))
        {
             String price = "Rs." + ads.get(position).price;
             holder.price.setText(price);
        } else {
            holder.price.setText("Donation");
        }

    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView ad_image;
        TextView title;
        TextView category;
        TextView price;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ad_image = itemView.findViewById(R.id.ad_item_img);
            title = itemView.findViewById(R.id.ad_title);
            category = itemView.findViewById(R.id.ad_category);
            price = itemView.findViewById(R.id.ad_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AdDetails.class);
                    intent.putExtra("id", ads.get(getAdapterPosition()).id);
                    context.startActivity(intent);
                }
            });

        }
    }
}
