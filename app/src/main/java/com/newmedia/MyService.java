package com.newmedia;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

public class MyService extends Service {
    private final String TAG = "MyService";
    private MediaPlayer mp = new MediaPlayer();
    private String openFilePath  =  "/sdcard/peipei.mp3";  //Environment.getExternalStorageDirectory().getPath() + "/peipei.mp3";
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub;
        // 初始化音乐资源
        Log.i(TAG, "cacacacacaca");
//        try {
//            Log.i(TAG, "create player");
//            initMediaPlayer();
//        } catch (IllegalStateException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        super.onCreate();
    }
    //初始化MediaPlayer
    private void initMediaPlayer() {
        try {
            //首先通过File对象指定音频文件的路径
            //此处示例中sd卡中的文件名为“music.mp3”
            File file = new File( openFilePath);
            Log.i(TAG, "kkkkkkkkkk--:"+file.getPath());
            //File file = new File("/存储盘/ComposeAudio/musicFile.mp3");
            mp.setDataSource(file.getPath());
            //让medieplayer进入准备状态
            mp.prepare();
        } catch (Exception e) {
            Log.i(TAG, "HAHAHA");
            e.printStackTrace();
            Log.i(TAG, "HAHAHA");
        }
    }



    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        // 开始播放音乐
        System.out.println("hahahahhahhah");
        //openFilePath = intent.getStringExtra("filepath");
        try{
            //openFilePath = openFilePath.replace("/sdcard/","/");
        }catch (IllegalStateException e){}
        try {
            Log.i(TAG, "create player");
            initMediaPlayer();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("gagaga:"+openFilePath);
        File file = new File( openFilePath);
        Log.i(TAG, "kkkkkkkkkk--:"+file.getPath());

        mp.start();
        // 音乐播放完毕的事件处理
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                // 循环播放
                try {
                    mp.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        // 播放音乐时发生错误的事件处理
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                // 释放资源
                try {
                    mp.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        // 服务停止时停止播放音乐并释放资源
        //mp.stop();
        mp.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}