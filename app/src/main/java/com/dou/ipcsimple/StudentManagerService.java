package com.dou.ipcsimple;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class StudentManagerService extends Service {

    private CopyOnWriteArrayList<Student> students = new CopyOnWriteArrayList<>();

    private static final String TAG = "StudentService";
    public StudentManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        students.add(new Student(1001, "Tom"));
        students.add(new Student(1002, "Jerry"));

        new Thread(new AddStudentWork()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("StudentManagerService", "Service onBind");
        int check = checkCallingOrSelfPermission("com.dou.ipcsimple.permission.ACCESS_STUDENT_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED) {
            Log.e("StudentManagerService", "Service onBind :" + null);
            return null;
        }
        return binder;
    }

    // 将会导致注销BUG，弃用
    //private CopyOnWriteArrayList<IOnStudentAddedListener> mListeners = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnStudentAddedListener> mListeners = new RemoteCallbackList<>();
    private Binder binder = new IStudentManager.Stub() {
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int check = checkCallingOrSelfPermission("com.dou.ipcsimple.permission.ACCESS_STUDENT_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            String pkgName = null;
            String[] pkgs = getPackageManager().getPackagesForUid(getCallingUid());
            if (null != pkgs && pkgs.length > 0) {
                pkgName = pkgs[0];
            }
            if (!pkgName.startsWith("com.dou")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void addStudent(Student student) throws RemoteException {
            students.add(student);
        }

        @Override
        public List<Student> getStudentList() throws RemoteException {
            //模拟耗时操作
//            SystemClock.sleep(20000);
            return students;
        }

        @Override
        public void registerListener(IOnStudentAddedListener listener) throws RemoteException {

            /** 旧版本的注册方法
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            } else {
                Log.d(TAG,"already exists.");
            }
             //*/
            mListeners.register(listener);
//            Log.d(TAG,"registerListener size:"+ mListeners.size());

        }

        @Override
        public void unregisterListener(IOnStudentAddedListener listener) throws RemoteException {
            /** 旧版本的注销方法，将会导致注销失败。
            if (mListeners.contains(listener)) {
                mListeners.remove(listener);
                Log.d(TAG,"unregister listener succeed.");
            } else {
                Log.w(TAG,"Warning:listener not found, cannot unregister.");
            }
             //*/
            mListeners.unregister(listener);
//            Log.d(TAG,"unregisterListener size:"+ mListeners.size());
        }


    };
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
    private class AddStudentWork implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int studentID = students.size() + 1;
                final Student student = new Student(studentID, "newStu_" + studentID);
//                new Thread() {
//                    @Override
//                    public void run() {
//
//                    }
//                }.start();
                try {
                    noticeStudentAdded(student);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void noticeStudentAdded(Student student) throws RemoteException {
        students.add(student);
        /** 旧版本
        Log.d(TAG, "noticeStudentAdded notify . listeners counts:" + mListeners.size());
        for (int i = 0; i < mListeners.size(); i++) {
            IOnStudentAddedListener listener = mListeners.get(i);
            listener.onStudentAdded(student);
        }
         //*/
        //RemoteCallbackList not-is a List...
        final int COUNT = mListeners.beginBroadcast();
        Log.d(TAG, "noticeStudentAdded notify . listeners counts:" + COUNT);
        for (int i = 0; i < COUNT; i++) {
            IOnStudentAddedListener listener = mListeners.getBroadcastItem(i);
            if (null != listener) {
                listener.onStudentAdded(student);
            }
        }
        mListeners.finishBroadcast();

    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "Service onDestroy");
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }
}
