package fina.com.servicetest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

public class MyService extends Service {

    class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private double param;
    private MyBinder myBinder;

    @Override
    public void onCreate() {
        Log.i(Context.ACTIVITY_SERVICE, "Service onCreate");
        super.onCreate();
        myBinder = new MyBinder();
    }

    //intent startcommand输入对象、flags 启动方式、startId 唯一标识符
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Context.ACTIVITY_SERVICE, "Service onStartCommand ");
        Log.i(Context.ACTIVITY_SERVICE, "Main thread id is " + Thread.currentThread().getId());
        final String name = intent.getStringExtra("Name");
        this.param = intent.getDoubleExtra("param", 1.0);
        //以异步方式进行模拟操作
        new Thread(new AsynvRunnable(){}).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Context.ACTIVITY_SERVICE, "onBind");
        return this.myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(Context.ACTIVITY_SERVICE, "onUnbind");
        //触发onRebind
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(Context.ACTIVITY_SERVICE, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(Context.ACTIVITY_SERVICE, "onDestroy");
        super.onDestroy();
    }

    public String getData() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().toString();
    }

    public double getValue(int value) {
        return value * param;
    }
}
