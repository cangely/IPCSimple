package com.dou.ipcsimple.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {

    private static final String TAG = "MessengerService";

    public MessengerService() {
    }

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerConst.MSG_FROM_CLIENT:
                    Log.d(TAG,"msg from client:" + msg.getData().getString("msg"));

                    Messenger client = msg.replyTo;
                    Message replyMsg = Message.obtain(null,MessengerConst.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    replyMsg.setData(bundle);
                    bundle.putString("reply", "来自服务断的回复");
                    try {
                        client.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    super.handleMessage(msg);

            }
        }
    }

    private static final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind");
        return mMessenger.getBinder();
    }
}
