package com.purplefrog.glitchclocka;

import android.os.*;

/**
 * Created by IntelliJ IDEA.
 * User: thoth
 * Date: 10/26/11
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class LearningState
    implements Parcelable
{

    public String name;
    public int totalTime;
    public long timeComplete;
    public String skillIconURL;
    public String description;

    LearningState()
    {
        totalTime = 24*60*60;
        timeComplete = System.currentTimeMillis()/1000 + totalTime;
    }

    LearningState(String name, long timeComplete, int totalTime, String skillIconURL, String description)
    {
        this.name = name;
        this.totalTime = totalTime;
        this.timeComplete = timeComplete;
        this.skillIconURL = skillIconURL;
        this.description = description;
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(name);
        parcel.writeLong(timeComplete);
        parcel.writeInt(totalTime);
        parcel.writeString(skillIconURL);
        parcel.writeString(description);
    }

    public static final Creator CREATOR = new Creator<LearningState>() {
        public LearningState createFromParcel(Parcel parcel)
        {
            return new LearningState(parcel.readString(), parcel.readLong(), parcel.readInt(), parcel.readString(), parcel.readString());
        }

        public LearningState[] newArray(int i)
        {
            return new LearningState[i];
        }
    };
}
