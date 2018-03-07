package fina.com.servicetest;

import android.content.Context;
import android.util.Log;

public class AsynvRunnable implements Runnable {
    @Override
    public void run() {
        try{
            Log.i(Context.ACTIVITY_SERVICE, "Async thread id is "+Thread.currentThread().getId());
            //虚拟操作
            for (int i=0;i<8;i++){
                Thread.sleep(1000);
                Log.i(Context.ACTIVITY_SERVICE, "-----Do work-----");
            }
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }
}
