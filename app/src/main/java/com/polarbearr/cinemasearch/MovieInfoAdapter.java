package com.polarbearr.cinemasearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.ViewHolder>{

    static Context context;
    List<MovieInfo> items = new ArrayList<>();

    OnItemClickListener listener;

    public static interface OnItemClickListener{
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    public MovieInfoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.movieinfo_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieInfoAdapter.ViewHolder holder, int position) {
        MovieInfo item = items.get(position);
        holder.setItem(item);

        holder.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(MovieInfo item){
        items.add(item);
    }

    public void addItems(List<MovieInfo> items){
        this.items = items;
    }

    public MovieInfo getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPoster;
        TextView tvTitle;
        RatingBar ratingBar;
        TextView tvPubDate;
        TextView tvDirector;
        TextView tvActor;

        OnItemClickListener listener;

        public ViewHolder(View itemView){
            super(itemView);

            imgPoster = itemView.findViewById(R.id.poster);
            tvTitle = itemView.findViewById(R.id.title);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvPubDate = itemView.findViewById(R.id.pubDate);
            tvDirector = itemView.findViewById(R.id.director);
            tvActor = itemView.findViewById(R.id.actor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(MovieInfo item){
            String imgUrl = item.getImageUrl();
            Glide.with(context).load(imgUrl).into(imgPoster);

            tvTitle.setText(Html.fromHtml(item.getTitle()));

            float userRating = (float)Math.round(item.getUserRating()) / 2;
            ratingBar.setRating(userRating);

            tvPubDate.setText(item.getPubDate());
            tvDirector.setText(item.getDirector());
            tvActor.setText(item.getActor());
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
    }
}
