package edu.northeastern.team21.Foggy.ImageRV;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.northeastern.team21.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<ImageItem> dataList;
    private Context context;
    private ShareButtonClickListener listener;

    public ImageAdapter(Context context, List<ImageItem> dataList) {
        this.context = context;
        this.dataList = dataList;

    }

    public void setOnItemClickListener(ShareButtonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerimage_item, parent, false);
        return new ImageViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem item = dataList.get(position);
        holder.city.setText(item.getCity());
        Glide.with(context).load(item.getImageURL()).into(holder.recyclerImage);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView recyclerImage;
        Button sharebtn;
        TextView city;


        public ImageViewHolder(@NonNull View itemView, ShareButtonClickListener listener) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.imageretived);
            city = itemView.findViewById(R.id.recyclercity);
            sharebtn = itemView.findViewById(R.id.button_share);

            sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onShareClick(position);
                        }
                    }
                }
            });

        }

    }
    public interface ShareButtonClickListener {

        void onShareClick(int position);
    }
}
