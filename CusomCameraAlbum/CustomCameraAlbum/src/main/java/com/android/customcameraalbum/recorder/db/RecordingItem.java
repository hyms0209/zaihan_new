package com.android.customcameraalbum.recorder.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 音频文件的存储
 * @author zhongjh
 */
public class RecordingItem implements Parcelable {
    /**
     * file name
     */
    private String mName;
    /**
     * file path
     */
    private String mFilePath;
    /**
     * id in database
     */
    private int mId;
    /**
     * length of recording in seconds
     */
    private int mLength;
    /**
     * date/time of the recording
     */
    private long mTime;

    public RecordingItem()
    {
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mFilePath);
        dest.writeInt(this.mId);
        dest.writeInt(this.mLength);
        dest.writeLong(this.mTime);
    }

    protected RecordingItem(Parcel in) {
        this.mName = in.readString();
        this.mFilePath = in.readString();
        this.mId = in.readInt();
        this.mLength = in.readInt();
        this.mTime = in.readLong();
    }

    public static final Creator<RecordingItem> CREATOR = new Creator<RecordingItem>() {
        @Override
        public RecordingItem createFromParcel(Parcel source) {
            return new RecordingItem(source);
        }

        @Override
        public RecordingItem[] newArray(int size) {
            return new RecordingItem[size];
        }
    };
}