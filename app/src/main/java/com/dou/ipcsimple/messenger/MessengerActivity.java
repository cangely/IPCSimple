package com.dou.ipcsimple.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dou.ipcsimple.R;

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";

    private Messenger mService;
    private boolean bindService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        findViewById(R.id.btnSendMsgToService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessengerActivity.this, MessengerService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            }
        });
    }

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerConst.MSG_FROM_SERVICE:
                    Log.d(TAG,"msg from client:" + msg.getData().getString("reply"));

                    break;
                default:
                        super.handleMessage(msg);
            }
        }
    }

    private Messenger replyMessenger = new Messenger(new MessengerHandler());
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bindService = true;
            mService = new Messenger(service);
            Message msg = Message.obtain(null,MessengerConst.MSG_FROM_CLIENT);
            Bundle bundle = new Bundle();
            bundle.putString("msg","Hi, i am client.");
            msg.setData(bundle);
            msg.replyTo = replyMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "remote exception");
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
            unbindService(mConnection);
        }
        super.onDestroy();
    }
}
