package com.example.finalnoteapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable {

    private String tagID;
    private String tagName;

    public Tag(String tagID, String tagName) {
        this.tagID = tagID;
        this.tagName = tagName;
    }

    protected Tag(Parcel in) {
        tagID = in.readString();
        tagName = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tagID);
        parcel.writeString(tagName);
    }
}
