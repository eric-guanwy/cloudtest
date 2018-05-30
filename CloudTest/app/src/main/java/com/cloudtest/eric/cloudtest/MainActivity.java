package com.cloudtest.eric.cloudtest;
import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.Buffer;
import static android.content.ContentValues.TAG;

/*
* 【Android 学习】实现仿360悬浮窗
*https://blog.csdn.net/u013132758/article/details/51222963
* */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startFloatWindow = (Button) findViewById(R.id.start_float_window);
        //如果没有系统允许的话，就发送一个action，向使用者要求使用权限，不然程式会崩溃
        //https://blog.csdn.net/chenlove1/article/details/52047105
        if (! Settings.canDrawOverlays(MainActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,10);
        }
        Toast.makeText(MainActivity.this,"welcome",Toast.LENGTH_SHORT).show();

        startFloatWindow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
                startService(intent);
                finish();
            }
        });
    }



    /**
     * 执行shell命令
     *
     * @param cmd
     */
    public final void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("ps");

            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
            //return "success";
        } catch (Throwable t) {
            t.printStackTrace();
            //return "failed";
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_device_info:{
                BufferedReader reader = null;
                String content = "";
                TextView device_info_area = findViewById(R.id.device_info_area);
                device_info_area.setText("");
                /*
                try{
                    Process process = Runtime.getRuntime().exec("ps");
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuffer output = new StringBuffer();
                    int read;
                    char[] buffer = new char[4096];
                    while ((read = reader.read(buffer)) >0 ) {
                        output.append(buffer,0,read);
                    }
                    reader.close();
                    content = output.toString();
                    device_info_area.setText(content);

                }catch (IOException e) {
                    e.printStackTrace();
                }*/

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        execShellCmd("am start com.tencent.mm/com.tencent.mm.ui.LauncherUI");
                        TextView device_info_area = findViewById(R.id.device_info_area);
                        device_info_area.setText("success");
                    }
                }).start();
                Toast.makeText(MainActivity.this,"haha not granted",Toast.LENGTH_SHORT).show();

            }
            break;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        Toast.makeText(this, "X at " + x + ";Y at " + y, Toast.LENGTH_SHORT).show();
        return true;
    }

    //这段是接收如果使用者不同意的话所做的相对应处理 https://blog.csdn.net/chenlove1/article/details/52047105
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= 23){
            if (requestCode == 10) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(MainActivity.this,"not granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}