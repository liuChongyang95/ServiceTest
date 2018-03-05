# ServiceTest
后台练习 笔记
Service 主要原因 1.简化后台任务的实现
                 2.实现同一台设备当中跨进程的远程通信

Local Service & Remote Service 本地和远程

本地 支持同一进程内的应用程序进行访问
远程 可以通过AIDL技术支持跨进程访问 

启动服务通过 Context.startService()（本地服务）
             Context.bindService()（本地服务、远程服务）

----
1.1 意义：提供数据支持，对其进行监听
实例：地理位置检测、SD卡扫描、当地气候定期检测。

*要添加异步操作

----
1.2 服务类型：

1.2.1
    生命周期模型分类：Context.startService()启动—Context.stopService()结束（缺乏灵活性）

                      Context.bindService()启动—Context.unbindService()结束（灵活。通过Ibinder借口中获取Service的句柄）

startService()启动本地服务,bindService()用于远程绑定.也可以混合使用，多次绑定。

1.2.2
    按寄存方式分类:本地服务 
                   寄存于当前进程中，当前进程结束Service结束。
                   不会自动启动线程，需要人工启动，之后Serivice将寄存于主线程。
 
                   远程服务 
                   寄存于另一个进程，通过AIDL（android interface definition language）定义接口语言，
                   实现Android设备的两个进程间通信（IPC）。
                   AIDL的IPC机制基于RPC（Remote process call）远程过程调用协议建立的，用于约束进程间的通讯规则，供编译器生成代码。
    
----
Service的生命周期

方法                                      说明

void onCreate（）                         当Service被启动时触发，整个生命周期内只启动一次。


int onStartCommand(Intent intent,         通过Context.startService启动服务时触发。
int flags,int startId)                    intent 是startCommand的输入对象，flags是启动方式，startId是唯一标志符。返回值决定结束的处理方式

void onStart（Intent intent,int startId） 内置于onStartCommand
 
IBinder onBind(Intent intent)             Context.bindService触发。返回一个IBinder对象，用于在远程服务时对Service进行远程操控
                  
void onRebind（Intent intent）            启动条件：startService和bindService启动。且onUnbind返回true时，下次bindService将触发。

boolean onUnbind(Intent intent)           调用Context.unbindService触发。默认返回false，返回true后，再次调用Context.bindService将触发onRebind

void onDestroy()                          1.startService—stopService 2.bindService—bindService 3. startService+bindService—unbindService+stopService
                                          (sS和bS谁先谁后无所谓)
----

onStartCommand方法

Service被kill后的处理方式:int onStartCommand的返回值

START_STICKY
如果Service被kill，系统会尝试重新创建，如果没有任何启动命令被传递到Service，intent将为null。

START_NOT_STICKY
使用这个返回值，如果在执行完onStartCommand（）后，服务被异常kill，系统不会重启该服务。

START_REDELIVER_INTENT
使用这个返回值，如果在执行完onStartCommand（）后，服务被异常kill，系统会自动重启该服务，并将intent的值传入。

START_STICKY_COMPATIBILITY
START_STICKY的兼容版,但不保证服务被kill后一定能重启。
输入参数flags代表此次onStartCommand的启动方式，正常启动，值为0;kill后重启，有两种情况： START_FLAG_RETRY 由于上次返回START_STICKY,所以参数intent 为 null。
                                                                                        START_FLAG_REDELIVERY 由于返回值为START_REDELIVER_INTENT,所以带输入参数intent。

----
2.2 Service的运作流程

Context.startService（intent）调用时：先触发Service的onCreate()，进行初始化处理，整个生命周期只触发一次。
                                      然后触发Service的onStartCommand()方法。每次调用startService都会调用。
                                      之后，Service 除非在资源不足的情况下被系统 kill 掉，否则Service不会自动结束，
                                      直至系统调用Context.stopService()方法时，Service 才会结束。
                                      在Service结束时将自动启动onDestory()方法对运转中的Service作最后处理。

Context.bindService()方法调用时：也会触发onCreate，然后触发onBind对服务进行绑定，成功获取Service句柄后，系统会通过
                                 用户自定义的serviceConnection对象onServiceConnected（ComponentName name，IBinder service）方法
                                 对Service对象作出处理。最后当系统调用unbindService时，就会激发onDestory做最后处理。
----
Service配置说明：
android:name　       服务类名，注意如果Service与Activity不在同一个包中，在android:name上必须写上Service的全路径
android:label　　    服务的名字，如果为空，默认显示的服务名为类名
android:icon　　     服务的图标
android:permission   申明此服务的权限，这意味着只有提供了该权限的应用才能控制或连接此服务
android:process　    表示该服务是否运行在另外一个进程，如果设置了此项，那么将会在包名后面加上这段字符串表示另一进程的名字
android:enabled　    如果此项设置为 true，那么 Service 将会默认被系统启动，默认值为 false
android:exported　   表示该服务是否能够被其他应用程序所控制或连接，默认值为 false
----
3.1 通过Context.bindService启动Service服务

IBinder是Binder远程对象的基本接口，这个接口定义了以远程对象的交互协议，但它不仅用于远程调用，也用于进程内调用。
系统可以通过IBinder获取Service的句柄。
ServiceConnection主要用于通过Binder绑定Service句柄后，对Service对象进行处理，它主要有两个方法
void onServiceConnected(ComponentName name,IBinder service)和
void onServiceDisconnected(ComponentName name)。
在Context.bindService()完成绑定后，系统就会调用onServiceConnected()方法，用户可以通过IBinder参数获取Service句柄，
对Service进行处理。而onServiceDisconnected()方法一般不会调用，只有Service被绑定后，由于内存不足等问题被kill才会调用。
----
3.3 Service服务的综合运用

目前更多的是结合startService()+bindService()两种方式调用Service服务：
    startService()负责管理Service服务的启动，输入初始化参数；
    bindService()负责定时对Service服务进行检测。
而且是规律性流程，以startService()启动服务后，每使用bindService()绑定服务，就通过serviceConnection对服务进行检测，
然后以unbindService()结束绑定。此时服务并未结束，而是长期运行于后台，直到系统以stopService()方法结束服务后,Service才会最终完结。

通过startService()启动服务后，多次使用bindService()绑定服务,unbindService()解除绑定，最后通过stopService()结束后：结果如下
onBind（）方法和onUnbind（）方法只在第一个bindService流程中触发，其后多次调用bindService()，此事件都不会被触发，
而只会触发onServiceConnected()。 因为默认情况下，系统在绑定时会先搜索IBinder接口，如果Service已经绑定了Binder对象，
系统就会直接跳过onBind()方法。

可以通过void onRebind(intent)方法，Service服务中boolean onUnbind（intent）的默认返回值为false，改为true，
那么，在第二次调用Context.bindService()的时候，就会激活Service.onRebind(intent)，去切换不同的绑定。
![Alt text](https://github.com/liuChongyang95/ServiceTest/raw/master/Screenshots/lifetime2.png)
![Alt text](https://github.com/liuChongyang95/ServiceTest/raw/master/Screenshots/ServiceLife.png)


ConstraintLayout 适合拖拽制作界面
