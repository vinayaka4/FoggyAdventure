package edu.northeastern.team21.StickIt;

public class Sticker {
    private int stickerId;
    private int stickerResourceId;
    private String stickerName;

    public Sticker() {

    }

    public Sticker(int id, String name) {
        this.stickerId = id;
        this.stickerName = name;
    }

    public Sticker(int stickerId, int stickerResourceId, String stickerName) {
        this.stickerId = stickerId;
        this.stickerResourceId = stickerResourceId;
        this.stickerName = stickerName;
    }

    public int getStickerId() {
        return stickerId;
    }

    public String getStickerName() {
        return stickerName;
    }

    public int getStickerResourceId() {
        return stickerResourceId;
    }

    public void setStickerResourceId(int stickerResourceId) {
        this.stickerResourceId = stickerResourceId;
    }
}
