package edu.northeastern.team21.Foggy;

public class FoggyUsers {
    public String name, username, useremail, dob;

    public FoggyUsers(String name, String username, String useremail, String dob) {
        this.name = name;
        this.username = username;
        this.useremail = useremail;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() {
        return useremail;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }
}
