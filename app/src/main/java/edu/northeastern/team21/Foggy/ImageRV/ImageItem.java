package edu.northeastern.team21.Foggy.ImageRV;

public class ImageItem {
    private String imageURL,city;

    public ImageItem(String imageURL,String city) {
        this.imageURL = imageURL;
        this.city =city;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
