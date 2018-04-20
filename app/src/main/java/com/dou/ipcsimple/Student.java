package com.dou.ipcsimple;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/10/31 0031.
 */

public class Student implements Parcelable {
    public String name;
    public int stuId;


    public Student(int stuId, String name) {
        this.name = name;
        this.stuId = stuId;
    }

    protected Student(Parcel in) {
        name = in.readString();
        stuId = in.readInt();
    }

    public Student() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(stuId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public String toString() {
        return String.format("[stuId:%s, name:%s]", stuId, name);
    }


}
