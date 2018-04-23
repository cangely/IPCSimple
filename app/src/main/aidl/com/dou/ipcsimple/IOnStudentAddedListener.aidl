package com.dou.ipcsimple;
import com.dou.ipcsimple.Student;

interface IOnStudentAddedListener {
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void onStudentAdded(in Student student);
}
