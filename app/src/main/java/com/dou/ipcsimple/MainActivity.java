package com.dou.ipcsimple;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dou.ipcsimple.messenger.MessengerActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "IPCSimple";
    private boolean bindService = false;

    private static final int MESSAGE_NEW_STUDENT_ADDED = 1;
    private IStudentManager remoteStudentManager;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_STUDENT_ADDED:
                    Log.d(TAG, "receive new student:" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btnSendRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentManagerService.class);
                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.btnEnterMessengerAct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessengerActivity.class);
                startActivity(intent);
            }
        });

    }

    private IOnStudentAddedListener mOnStudentAdded = new IOnStudentAddedListener.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void onStudentAdded(Student student) throws RemoteException {
            handler.obtainMessage(MESSAGE_NEW_STUDENT_ADDED, student).sendToTarget();
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bindService = true;
            IStudentManager studentManager = IStudentManager.Stub.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient,0);
                remoteStudentManager = studentManager;
                List<Student> students = studentManager.getStudentList();
                Log.d(TAG, "Client Request students:" + students);

                Student student = new Student(1003,"Jack");
                studentManager.addStudent(student);
                students = studentManager.getStudentList();
                Log.d(TAG, "Client Request students:" + students);

                studentManager.registerListener(mOnStudentAdded);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteStudentManager = null;
            Log.d(TAG, "binder died.");
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (remoteStudentManager == null) {
                return;
            }
            remoteStudentManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            remoteStudentManager = null;
            //重新绑定远程服务
            Intent intent = new Intent(MainActivity.this, StudentManagerService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    };
    @Override
    protected void onDestroy() {
        if (remoteStudentManager != null && remoteStudentManager.asBinder().isBinderAlive()) {
            Log.i(TAG, "unregister listener:" + mOnStudentAdded);
            try {
                remoteStudentManager.unregisterListener(mOnStudentAdded);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (bindService){
            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }
}
