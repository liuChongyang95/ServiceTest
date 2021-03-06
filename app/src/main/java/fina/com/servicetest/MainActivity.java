package fina.com.servicetest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button startS;
    private Button stopS;
    private Button bindS;
    private Button unbindS;
    private static boolean connected = false;

    private myServiceConnection myServiceConnection = new myServiceConnection();

    class myServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(Context.ACTIVITY_SERVICE, "Service Connected");
            String data = null;
            //通过IBinder获取service句柄
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            MyService myService = myBinder.getService();
            //生出随机数
            Random random = new Random();
            double value = myService.getValue(random.nextInt(10) * 1000);
            //显示计算结果
            data = myService.getData();
            Log.i(Context.ACTIVITY_SERVICE, String.valueOf(value));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(Context.ACTIVITY_SERVICE, "Disconnected");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startS = findViewById(R.id.startService);
        stopS = findViewById(R.id.stopService);
        bindS = findViewById(R.id.bindService);
        unbindS = findViewById(R.id.unbindService);
        myServiceConnection = new myServiceConnection();
    }

    public void btnStart_onClick(View view) {
        //Intent 绑定MyService，加入输入参数
        Intent intent = new Intent(MainActivity.this, MyService.class);
        intent.putExtra("Name", "Leslie");
        intent.putExtra("param", 0.88);
        //启动service
        startService(intent);
        Intent intent1 = new Intent(this, MyIntentService.class);
        intent1.putExtra("msg", "intentService1");
        startService(intent1);
        Intent intent2 = new Intent(this, MyIntentService.class);
        intent2.putExtra("msg", "intentService2");
        startService(intent2);
        Intent intent3 = new Intent(this, MyIntentService.class);
        intent3.putExtra("msg", "intentService3");
        startService(intent3);

    }

    public void btnStop_onClick(View view) {
        Intent intent = new Intent(MainActivity.this, MyService.class);
        stopService(intent);
        Intent intent1 = new Intent(this, MyIntentService.class);
        stopService(intent1);
    }

    public void btnBind(View view) {
        connected = true;
        Intent intent = new Intent(this, MyService.class);
        //bind方式启动
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void btnUnBind(View view) {
        if (connected) {
            unbindService(myServiceConnection);
            connected = false;
        }
    }
}
