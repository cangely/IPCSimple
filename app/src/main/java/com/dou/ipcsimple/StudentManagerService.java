package com.dou.ipcsimple;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StudentManagerService extends Service {

    private CopyOnWriteArrayList<Student> students = new CopyOnWriteArrayList<>();
    public StudentManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        students.add(new Student(1001,"Tom"));
        students.add(new Student(1002, "Jerry"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    private Binder binder = new IStudentManager.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void addStudent(Student student) throws RemoteException {
            students.add(student);
        }

        @Override
        public List<Student> getStudentList() throws RemoteException {
            return students;
        }
    };
}
