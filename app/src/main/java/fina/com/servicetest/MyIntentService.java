package fina.com.servicetest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyIntentService extends IntentService {
    public MyIntentService(){
        super(null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msg=intent.getStringExtra("msg");
        Log.i(Context.ACTIVITY_SERVICE, "msg's thread id is "+Thread.currentThread().getId());
    }
}
