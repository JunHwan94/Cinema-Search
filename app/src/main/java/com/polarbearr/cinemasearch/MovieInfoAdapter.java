package com.polarbearr.cinemasearch;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.polarbearr.cinemasearch.data.MovieInfo;
import com.polarbearr.cinemasearch.databinding.MovieinfoItemBinding;

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
        this.items.addAll(items);
    }

    public MovieInfo getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        MovieinfoItemBinding binding = DataBindingUtil.bind(itemView);

        OnItemClickListener listener;

        public ViewHolder(View itemView){
            super(itemView);

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
            Glide.with(context).load(imgUrl).into(binding.imgvPoster);

            binding.tvTitle.setText(Html.fromHtml(item.getTitle()));

            float userRating = (float)Math.round(item.getUserRating()) / 2;
            binding.ratingBar.setRating(userRating);

            binding.tvPubDate.setText(item.getPubDate());
            binding.tvDirector.setText(item.getDirector());
            binding.tvActor.setText(item.getActor());
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
    }
}
