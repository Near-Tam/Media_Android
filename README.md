# Media_Android
一个程序实现 播放音乐（可以选定目录文件播放），录音，测试功耗（监控CPU的占用率和内存占用）三个功能
## Activity-Service通信：
`Intent regIntent = new Intent(MainActivity.this, MyService.class);
 startService(regIntent);
 MainActivity.this.stopService(regIntent);`
 
 ## Activity1-Activity2通信（当前在Activity1,目标Activity2）：
 `Intent intent = new Intent(Activity1.this, Activity2.class);
  Activity1.this.startActivity(intent);`
