package com.example.finalnoteapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class Note implements Parcelable{
    public static final int TYPE_LIST = 1;
    public static final int TYPE_GRID = 2;
    private int typeDisplay;

    private String title;
    private List<String> content;
    private Boolean prioritize;
    private List<String> reminder;
    private String password;
    private String label_name;
    private String user_id;

    public Note(String user_id) {
        this.user_id = user_id;
    }
    public Note(String title, List<String> content, Boolean prioritize, List<String> reminder, String password, String label_name, String user_id) {
        this.title = title;
        this.content = content;
        this.prioritize = prioritize;
        this.reminder = reminder;
        this.password = password;
        this.label_name = label_name;
        this.user_id = user_id;
    }


    public static int getTypeList() {
        return TYPE_LIST;
    }

    public static int getTypeGrid() {
        return TYPE_GRID;
    }

    public int getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(int typeDisplay) {
        this.typeDisplay = typeDisplay;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public Boolean getPrioritize() {
        return prioritize;
    }

    public void setPrioritize(Boolean prioritize) {
        this.prioritize = prioritize;
    }

    public List<String> getReminder() {
        return reminder;
    }

    public void setReminder(List<String> reminder) {
        this.reminder = reminder;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static Creator<Note> getCREATOR() {
        return CREATOR;
    }

    protected Note(Parcel in) {
        typeDisplay = in.readInt();
        title = in.readString();
        content = in.createStringArrayList();
        byte tmpPrioritize = in.readByte();
        prioritize = tmpPrioritize == 0 ? null : tmpPrioritize == 1;
        reminder = in.createStringArrayList();
        password = in.readString();
        label_name = in.readString();
        user_id = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(typeDisplay);
        parcel.writeString(title);
        parcel.writeStringList(content);
        parcel.writeByte((byte) (prioritize == null ? 0 : prioritize ? 1 : 2));
        parcel.writeStringList(reminder);
        parcel.writeString(password);
        parcel.writeString(label_name);
        parcel.writeString(user_id);
    }
}
