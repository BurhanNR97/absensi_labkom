package com.absensi.labkom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class modelUser implements Parcelable {

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String username;
    private String password;
    private String level;

    public modelUser() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.level);
    }

    protected modelUser(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.level = in.readString();
    }

    public static final Parcelable.Creator<modelUser> CREATOR = new Parcelable.Creator<modelUser>() {
        @Override
        public modelUser createFromParcel(Parcel source) {
            return new modelUser(source);
        }

        @Override
        public modelUser[] newArray(int size) {
            return new modelUser[size];
        }
    };
}
