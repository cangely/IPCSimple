// IStuManager.aidl
package com.dou.ipcsimple;
import com.dou.ipcsimple.Student;
import com.dou.ipcsimple.IOnStudentAddedListener;

interface IStudentManager {
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void addStudent(in Student student);
    List<Student> getStudentList();
    //新增的接口，用于注册和注销回调接口
    void registerListener(IOnStudentAddedListener listener);
    void unregisterListener(IOnStudentAddedListener listener);

}
