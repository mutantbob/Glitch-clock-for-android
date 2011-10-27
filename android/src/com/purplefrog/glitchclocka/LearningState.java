/*
    Copyright (C) 2011 Robert Forsman

    This file is part of Glitch clock for android.

    Glitch clock for android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Glitch clock for android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Glitch clock for android.  If not, see <http://www.gnu.org/licenses/>.
 */

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
