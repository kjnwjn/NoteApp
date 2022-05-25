package com.example.finalnoteapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class Note{
    public static final int TYPE_LIST = 1;
    public static final int TYPE_GRID = 2;
    private int typeDisplay;

    private String title;
    private String text;
    private String image;
    private String audio;
    private String video;
    private List<String> listLabel;
    private boolean hasPassword;
    private String password;
    private String remindTime;
    private boolean inTrash;
    private String dateInTrash;


    public Note(String title, String text, String image, String audio, String video, List<String> listLabel, boolean hasPassword, String password, String remindTime, boolean inTrash, String dateInTrash) {
        this.title = title;
        this.text = text;
        this.image = image;
        this.audio = audio;
        this.video = video;
        this.listLabel = listLabel;
        this.hasPassword = hasPassword;
        this.password = password;
        this.remindTime = remindTime;
        this.inTrash = inTrash;
        this.dateInTrash = dateInTrash;
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
}
