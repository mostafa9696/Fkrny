package com.example.mostafahussien.fkrny.Model;

/**
 * Created by Mostafa Hussien on 06/09/2017.
 */

public class Reminder {
    // Reminder types
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 2;

    // Repetition types
    public static final int DOES_NOT_REPEAT = 0;
    public static final int HOURLY = 1;
    public static final int DAILY = 2;
    public static final int WEEKLY = 3;
    public static final int MONTHLY = 4;
    public static final int YEARLY = 5;
    public static final int SPECIFIC_DAYS = 6;
    public static final int ADVANCED = 7;

    private int id;
    private String title;
    private String content;
    private String dateAndTime;
    private int repeatType;
    private int numberToShow;
    private int numberShown;
    private String icon;
    private String colour;
    private boolean[] daysOfWeek;  // if daysOfWeek[0]=true --> then task will repeat on sunday
    private int interval;
    private boolean isSelected=false;

    public static int getACTIVE() {
        return ACTIVE;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public static int getINACTIVE() {
        return INACTIVE;
    }
    public String getDate() {
        return dateAndTime.substring(0, 8);
    }
    public static int getDoesNotRepeat() {
        return DOES_NOT_REPEAT;
    }

    public static int getHOURLY() {
        return HOURLY;
    }

    public static int getDAILY() {
        return DAILY;
    }

    public static int getWEEKLY() {
        return WEEKLY;
    }

    public static int getMONTHLY() {
        return MONTHLY;
    }

    public static int getYEARLY() {
        return YEARLY;
    }

    public static int getSpecificDays() {
        return SPECIFIC_DAYS;
    }

    public static int getADVANCED() {
        return ADVANCED;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public int getNumberToShow() {
        return numberToShow;
    }

    public int getNumberShown() {
        return numberShown;
    }

    public String getIcon() {
        return icon;
    }

    public String getColour() {
        return colour;
    }

    public boolean[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public int getInterval() {
        return interval;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }


    public void setNumberToShow(int numberToShow) {
        this.numberToShow = numberToShow;
    }

    public void setNumberShown(int numberShown) {
        this.numberShown = numberShown;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setDaysOfWeek(boolean[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
