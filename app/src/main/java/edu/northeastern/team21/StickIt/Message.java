package edu.northeastern.team21.StickIt;

import java.util.HashMap;
import java.util.Map;

public class Message extends Sticker {
    private String sender;
    private String receiver;
    private String dateTime;

    public Message() {
    }

    public Message(String sender, String receiver, String dateTime, int stickerId, String stickerName) {
        super(stickerId, stickerName);
        this.sender = sender;
        this.receiver = receiver;
        this.dateTime = dateTime;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("receiver", receiver);
        result.put("dateTime", dateTime);
        result.put("stickerId", getStickerId());
        result.put("stickerName", getStickerName());

        return result;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDateTime() {
        return dateTime;
    }
}
