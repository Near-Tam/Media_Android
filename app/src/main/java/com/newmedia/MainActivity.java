package com.newmedia;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button stop;
    public TextView textCPU;
    public TextView textAppProcess;

    private Button button_start;
    private Button button_stop;
    private MediaRecorder recorder;

    private Button select;

    private int[] AppProList;
    private float[] CPUList;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stop = (Button) findViewById(R.id.stop);
        select = (Button) findViewById((R.id.button_openfile));
        stop.setOnClickListener(this);
        select.setOnClickListener(this);

        mHandler.obtainMessage().sendToTarget();
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        AppProList = new int[10];
        CPUList = new float[10];
        for(int i=0;i<AppProList.length;i++){
            AppProList[i] = 0;
            CPUList[i] = 0;
        }
    }


    private void init() {
        button_start = (Button) this.findViewById(R.id.record_start);
        button_stop = (Button) this.findViewById(R.id.record_stop);
        button_stop.setOnClickListener(this);
        button_start.setOnClickListener(this);
    }


    public void onClick(View v) {
        Intent intentSv = new Intent(MainActivity.this, MyService.class);
        Intent intent = new Intent(MainActivity.this, openFileActivity.class);
        switch (v.getId()) {
            case R.id.stop:
                MainActivity.this.stopService(intentSv);
                break;
            case R.id.record_start:
                MainActivity.this.stopService(intentSv);
                initializeAudio();
                break;
            case R.id.record_stop:
                recorder.stop();// 停止刻录
                recorder.release(); // 刻录完成一定要释放资源
                break;
            case R.id.button_openfile:
                //MainActivity.this.stopService(intentSv);
                //MainActivity.this.startActivity(intent);
                final Intent regIntent = new Intent(MainActivity.this, MyService.class);
                startService(regIntent);
                break;
        }


    }


    protected void onDestroy() {

        Intent intentSV = new Intent(this, MyService.class);
        super.onDestroy();
        stopService(intentSV);
        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }
    }


    private void initializeAudio() {
        recorder = new MediaRecorder();// new出MediaRecorder对象
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置MediaRecorder的音频源为麦克风
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        // 设置MediaRecorder录制的音频格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置MediaRecorder录制音频的编码为amr.
        recorder.setOutputFile("/sdcard/peipei.mp3");
        // 设置录制好的音频文件保存路径
        try {
            recorder.prepare();// 准备录制
            recorder.start();// 开始录制
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            refresh(); // 在refresh方法中更新视图上
            getRunningAppProcessInfo();
            mHandler.sendMessageDelayed(mHandler.obtainMessage(), 100);
        }

    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public void refresh() {

        float cpuRate = getProcessCpuRate();
        int AppProcess = getRunningAppProcessInfo();
        String strAppProcess = "";
        String strCPU = "";

        AppProList[AppProList.length-1] = AppProcess;
        CPUList[CPUList.length-1] = cpuRate;

        for(int i=0;i<AppProList.length-1;i++){
            strAppProcess = "\n" + AppProList[i] + "KB" + strAppProcess ;
            AppProList[i] = AppProList[i+1];

            strCPU = "\n" + CPUList[i] + "%" + strCPU;
            CPUList[i] = CPUList[i+1];

        }
        strAppProcess = "\n" + AppProList[AppProList.length-1] + "KB" + strAppProcess;
        strCPU = "\n" + CPUList[CPUList.length-1] + "%" + strCPU;

        textCPU = (TextView) findViewById(R.id.CPU);
        strCPU = "CPU占用率:" + strCPU;
        //TextView tv = new TextView();

        textAppProcess = (TextView) findViewById(R.id.AppProcess);
        strAppProcess = "内存占用:" + strAppProcess;


        textCPU.setText(strCPU);
        textAppProcess.setText(strAppProcess);
    }

    public static float getProcessCpuRate() {

        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(360);

        } catch (Exception e) {
        }

        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();

        float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);

        return cpuRate;
    }

    public static long getTotalCpuTime() { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }

    public static long getAppCpuTime() { // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try {
            int pid = Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }


    //获取占用内存
    private int getRunningAppProcessInfo() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memorySizeReturn = 0;
        //获得系统里正在运行的所有进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
            // 进程ID号
            int pid = runningAppProcessInfo.pid;
            // 用户ID
            int uid = runningAppProcessInfo.uid;
            // 进程名
            String processName = runningAppProcessInfo.processName;
            // 占用的内存
            int[] pids = new int[]{pid};
            Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
            int memorySize = memoryInfo[0].dalvikPrivateDirty;
            System.out.println("processName=" + processName + ",pid=" + pid + ",uid=" + uid + ",memorySize=" + memorySize + "kb");
            memorySizeReturn = memorySize;
        }
        return memorySizeReturn;
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.servicesmedia/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.newmedia/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.newmedia/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.servicesmedia/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

}

