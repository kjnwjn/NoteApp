package com.example.finalnoteapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class Note implements Parcelable {
    public static final int TYPE_LIST = 1;
    public static final int TYPE_GRID = 2;
    private int typeDisplay;

    private String noteID;
    private String title;
    private String text;
    private String image;
    private String audio;
    private String video;
    private List<String> listLabel;
    private boolean pin;
    private boolean hasPassword;
    private String password;
    private String remindTime;
    private boolean inTrash;
    private String dateInTrash;


    public Note(String noteID, String title, String text, String image, String audio, String video, List<String> listLabel, boolean pin, boolean hasPassword, String password, String remindTime, boolean inTrash, String dateInTrash) {
        this.noteID = noteID;
        this.title = title;
        this.text = text;
        this.image = image;
        this.audio = audio;
        this.video = video;
        this.listLabel = listLabel;
        this.pin = pin;
        this.hasPassword = hasPassword;
        this.password = password;
        this.remindTime = remindTime;
        this.inTrash = inTrash;
        this.dateInTrash = dateInTrash;
    }

    protected Note(Parcel in) {
        typeDisplay = in.readInt();
        noteID = in.readString();
        title = in.readString();
        text = in.readString();
        image = in.readString();
        audio = in.readString();
        video = in.readString();
        listLabel = in.createStringArrayList();
        pin = in.readByte() != 0;
        hasPassword = in.readByte() != 0;
        password = in.readString();
        remindTime = in.readString();
        inTrash = in.readByte() != 0;
        dateInTrash = in.readString();
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

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public List<String> getListLabel() {
        return listLabel;
    }

    public void setListLabel(List<String> listLabel) {
        this.listLabel = listLabel;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public boolean isInTrash() {
        return inTrash;
    }

    public void setInTrash(boolean inTrash) {
        this.inTrash = inTrash;
    }

    public String getDateInTrash() {
        return dateInTrash;
    }

    public void setDateInTrash(String dateInTrash) {
        this.dateInTrash = dateInTrash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(typeDisplay);
        parcel.writeString(noteID);
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeString(image);
        parcel.writeString(audio);
        parcel.writeString(video);
        parcel.writeStringList(listLabel);
        parcel.writeByte((byte) (pin ? 1 : 0));
        parcel.writeByte((byte) (hasPassword ? 1 : 0));
        parcel.writeString(password);
        parcel.writeString(remindTime);
        parcel.writeByte((byte) (inTrash ? 1 : 0));
        parcel.writeString(dateInTrash);
    }
}
