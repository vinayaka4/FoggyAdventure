package edu.northeastern.team21.StickIt;

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


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MessageHolder> {
    private final List<Message> messageList = new ArrayList<>();

    public void insertToHead(Message message) {
        messageList.add(0, message);
        notifyItemInserted(0);
    }
    
    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.historydetail, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message = messageList.get(position);

        holder.stickerId.setText("Sticker: " + message.getStickerName());
        holder.sticker.setImageResource(message.getStickerResourceId());
        holder.sender.setText("Send By: " + message.getSender());
        holder.sendTime.setText("Sent At: " + message.getDateTime());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {

        public TextView stickerId;
        public ImageView sticker;
        public TextView sender;
        public TextView sendTime;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            stickerId = itemView.findViewById(R.id.stickerName);
            sticker = itemView.findViewById(R.id.stickerImage);
            sender = itemView.findViewById(R.id.textStickerSender);
            sendTime = itemView.findViewById(R.id.textStickerTime);
        }
    }
}
