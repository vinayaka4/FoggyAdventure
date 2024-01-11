package edu.northeastern.team21.MovieRV;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.team21.R;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    private List<Movie> list = new ArrayList<>();

    @NonNull
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moviedetail, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder movieHolder, int position) {
        Movie movie = list.get(position);

        movieHolder.title.setText(movie.getTitle());
        movieHolder.year.setText(movie.getYear());
        movieHolder.type.setText(movie.getType());

        switch (movie.getType()) {
            case "movie":
                movieHolder.typeImage.setImageResource(R.drawable.icon_movie);
                break;
            case "series":
                movieHolder.typeImage.setImageResource(R.drawable.icon_series);
                break;
            case "episode":
                movieHolder.typeImage.setImageResource(R.drawable.icon_episode);
                break;
        }

        Picasso.get().load(movie.getPoster()).into(movieHolder.poster);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMovies(@Nullable List<Movie> repos) {
        if (repos == null) {
            return;
        }
        list.clear();
        list.addAll(repos);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearMovies() {
        list.clear();
        notifyDataSetChanged();
    }


    public static class MovieHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView year;
        public TextView type;
        public ImageView poster;
        public ImageView typeImage;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtmovietitle);
            year = itemView.findViewById(R.id.textyear);
            type = itemView.findViewById(R.id.texttype);
            typeImage = itemView.findViewById(R.id.imageViewType);
            poster = itemView.findViewById(R.id.poster);
        }
    }
}
