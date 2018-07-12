package com.byron.criminalintent;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

public class Crime implements Comparable<Crime>{
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;
    private int mPosition;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
        mTitle = "Untitled crime";
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public String getFormatDate() {
        CharSequence dateFormated = DateFormat.format("EEEE, MMM dd, yyyy", mDate);

        return dateFormated.toString();
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mPosition);
        builder.append(",");
        builder.append(mTitle);
        builder.append(",");
        builder.append(mId.toString());
        builder.append(",");
        builder.append(mDate.toString());
        return builder.toString();
    }

    @Override
    public int compareTo(@NonNull Crime o) {
        if(o.getId().equals(mId) &&
                mTitle.equals(o.getTitle()) &&
                mDate.equals(o.getDate()) &&
                mSolved == o.isSolved()) {
            return  0;
        } else {
            return 1;
        }
    }
}
