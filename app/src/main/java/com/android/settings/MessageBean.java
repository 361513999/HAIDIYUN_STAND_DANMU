package com.android.settings;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ekko on 2018/4/24.
 */

public class MessageBean implements Parcelable {
    private String filename;//名字
    private String url;//点击图片需要跳转的url
    private String filepath;//图片的路径
    private String time;//时间
    private int count;//次数
    private String type;//类型
    private long staytime;//停留的时间
    private long JumpTimes;//跳转的次数

    public long getStaytime() {
        return staytime;
    }

    public void setStaytime(long staytime) {
        this.staytime = staytime;
    }

    public long getJumpTimes() {
        return JumpTimes;
    }

    public void setJumpTimes(long jumpTimes) {
        JumpTimes = jumpTimes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    protected MessageBean(Parcel in) {
        this.filename = in.readString();
        this.url = in.readString();
        this.filepath = in.readString();
        this.time = in.readString();
        this.count = in.readInt();
        this.type=in.readString();
        this.staytime=in.readLong();
        this.JumpTimes=in.readLong();
    }

    public static final Creator<MessageBean> CREATOR = new Creator<MessageBean>() {
        @Override
        public MessageBean createFromParcel(Parcel in) {
            return new MessageBean(in);
        }

        @Override
        public MessageBean[] newArray(int size) {
            return new MessageBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public MessageBean() {
    }

    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        this.filename = dest.readString();
        this.count = dest.readInt();
        this.filepath = dest.readString();
        this.url = dest.readString();
        this.time = dest.readString();
        this.type=dest.readString();
        this.staytime=dest.readLong();
        this.JumpTimes=dest.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filename);
        dest.writeString(this.url);
        dest.writeString(this.filepath);
        dest.writeString(this.time);
        dest.writeInt(this.count);
        dest.writeString(this.type);
        dest.writeLong(this.staytime);
        dest.writeLong(this.JumpTimes);
    }

    @Override
    public String toString() {

        return "{" + "\"filename\":" + '\"' + filename + '\"'
                + ",\"url\":" + '\"' + url + '\"'
                + ",\"filepath\":" + '\"' + filepath + '\"'
                + ",\"time\":" + '\"' + time + '\"'
                + ",\"type\":" + '\"' + type + '\"'
                + ",\"staytime\":" + '\"' + staytime + '\"'
                + ",\"JumpTimes\":" + '\"' + JumpTimes + '\"'
                + ",\"count\":" + count + '}';
    }
}
