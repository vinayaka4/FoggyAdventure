package edu.northeastern.team21.StickIt.StickerRV;

import edu.northeastern.team21.StickIt.Sticker;

public class StickerCard extends Sticker {

    private int sentCount;

    public StickerCard(int stickerId, int stickerResourceId, String name) {
        super(stickerId, stickerResourceId, name);
        this.sentCount = 0;
    }

    public int getSentCount() {
        return sentCount;
    }

    public void setSentCount(int sentCount) {
        this.sentCount = sentCount;
    }
}
