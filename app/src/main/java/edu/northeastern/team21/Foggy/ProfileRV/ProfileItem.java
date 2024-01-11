package edu.northeastern.team21.Foggy.ProfileRV;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

public class ProfileItem {
    // TODO: the icon may not be a String, should be a Drawable datatype
    private Drawable icon; // get from local, should be a dr

    //@SerializedName("ww")
    private final String info; //get from firebase, no use for now
    // may not be a String

    public ProfileItem(Drawable icon, String info){
        this.icon = icon;
        this.info = info;
    }


    public Drawable getIcon() {
        return icon;
    }

    public String getInfo() {
        return info;
    }
}

