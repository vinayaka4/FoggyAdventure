package edu.northeastern.team21.StickIt.StickerRV;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.team21.R;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerHolder> {
    private final List<StickerCard> stickerList = new ArrayList<>();

    private StickerClickListener listener;

    public void setStickerList(List<StickerCard> list) {
        stickerList.clear();
        stickerList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnStickerClickListener(StickerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StickerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stickerdetail, parent, false);
        return new StickerHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerHolder holder, int position) {
        StickerCard sticker = stickerList.get(position);

        holder.stickerId.setText("sticker: " + sticker.getStickerName());
        holder.sticker.setImageResource(sticker.getStickerResourceId());
        holder.sentCount.setText("sent by this user: " + String.valueOf(sticker.getSentCount()));
    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public static class StickerHolder extends RecyclerView.ViewHolder {

        public TextView stickerId;
        public ImageView sticker;
        public TextView sentCount;

        public StickerHolder(@NonNull View itemView, StickerClickListener listener) {
            super(itemView);

            // bind the layout to attributes
            stickerId = itemView.findViewById(R.id.stickerName);
            sticker = itemView.findViewById(R.id.stickerImage);
            sentCount = itemView.findViewById(R.id.textViewCount);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int pos = getLayoutPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onStickerClick(pos);
                    }
                }
            });
        }
    }

    public interface StickerClickListener {

        void onStickerClick(int position);
    }

}
