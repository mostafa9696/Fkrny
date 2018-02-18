package com.example.mostafahussien.fkrny.Model;

public class TaskColour {

    private int colour;
    private String dateAndTime;

    public TaskColour(int colour, String dateAndTime) {
        this.colour = colour;
        this.dateAndTime = dateAndTime;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public int getColour() {
        return colour;
    }
}