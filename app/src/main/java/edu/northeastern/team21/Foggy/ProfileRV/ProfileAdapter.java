package edu.northeastern.team21.Foggy.ProfileRV;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.team21.MovieRV.Movie;
import edu.northeastern.team21.MovieRV.MovieAdapter;
import edu.northeastern.team21.R;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileItemHolder> {
    private List<ProfileItem> list = new ArrayList<>();

    private Context context;

    private ItemClickListener itemClickListener;



    public ProfileAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    public ProfileItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profileitemdetail, parent, false);
        return new ProfileItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileItemHolder profileItemHolder, int position) {
        ProfileItem profileItem = list.get(position);

        // TODO: should use different drawable according to the position. switch
        Drawable iconDrawable = profileItem.getIcon();

        profileItemHolder.iconImage.setImageDrawable(iconDrawable);

        profileItemHolder.infoContent.setText(profileItem.getInfo());

        //Picasso.get().load(movie.getPoster()).into(movieHolder.poster);
    }

    @Override
    public int getItemCount() { return this.list.size(); }

    @SuppressLint("NotifyDataSetChanged")
    public void setProfileItems(@Nullable List<ProfileItem> repos){
        if(repos==null) return;
        list.clear();
        list.addAll(repos);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearProfileItems() {
        list.clear();
        notifyDataSetChanged();
    }



    public class ProfileItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView iconImage;
        public TextView infoContent;

        private final String TAG = "ProfileItemHolder";
        public ProfileItemHolder(@NonNull View itemView) {
            super(itemView);
            this.iconImage = itemView.findViewById(R.id.imageView_profileIcon);
            this.infoContent = itemView.findViewById(R.id.textView_profileInfo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(itemClickListener!=null) itemClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    public void setClickListner(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}



