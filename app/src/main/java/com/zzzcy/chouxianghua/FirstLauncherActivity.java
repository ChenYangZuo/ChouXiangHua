package com.zzzcy.chouxianghua;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirstLauncherActivity extends AppCompatActivity {

    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("MyTaps","FirstLauncherActivity已启动");
        new download().execute("http://116.62.147.56:8085/directory.json");
        intent=new Intent(FirstLauncherActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class download extends AsyncTask<String,Void,Boolean> {

        ProgressDialog  dialog = new ProgressDialog(FirstLauncherActivity.this);

        @Override
        protected void onPreExecute(){
            Log.i("MyTaps","准备初始化下载");

            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.incrementProgressBy(20);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.i("MyTaps","开始初始化下载");
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestProperty("Charset", "UTF-8");
                con.setRequestMethod("GET");
                if (con.getResponseCode() == 200) {
                    InputStream is = con.getInputStream();//获取输入流
                    FileOutputStream fileOutputStream = null;//文件输出流
                    if (is != null) {
                        FileUtils fileUtils = new FileUtils();
                        fileOutputStream = new FileOutputStream(fileUtils.createFile("dictionary.json"));//指定文件保存路径，代码看下一步
                        byte[] buf = new byte[1024];
                        int ch;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                        }
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }
                Log.i("MyTaps","下载成功");
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e("MyTaps","下载失败");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialog.dismiss();
            cancel(true);
        }
    }
}