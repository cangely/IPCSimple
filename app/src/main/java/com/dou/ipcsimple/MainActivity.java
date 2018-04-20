package com.dou.ipcsimple;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "IPCSimple";
    private boolean bindService = false;
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
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bindService = true;
            IStudentManager studentManager = IStudentManager.Stub.asInterface(service);
            try {
                List<Student> students = studentManager.getStudentList();
                Log.d(TAG, "Client Request students:" + students);

                Student student = new Student(1003,"Jack");
                studentManager.addStudent(student);
                Log.d(TAG, "Client Request students:" + students);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onDestroy() {
        if (bindService){
            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }
}
