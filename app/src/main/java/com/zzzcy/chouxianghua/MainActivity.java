package com.zzzcy.chouxianghua;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {

    public static String Dictionary;

    public EditText renhua;
    public EditText chouxianghua;
    public Toolbar toolbar;
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MyTaps","跳转到main");

        renhua = (EditText)findViewById(R.id.renhua);
        chouxianghua = (EditText)findViewById(R.id.chouxianghua);
        toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);

        setSupportActionBar(toolbar);

        Log.i("MyTaps","读入字典");
        ReadTextFromSDcard();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chouxianghua.setText(Translator(renhua.getText().toString()));
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", chouxianghua.getText().toString());
                cm.setPrimaryClip(mClipData);
                Toast.makeText(MainActivity.this, "已经复制到剪切板中", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_top_about:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("关于");
                dialog.setMessage("其实根本没有孙笑川，或者说人人都是孙笑川。\n\nVersion:2020-1-22 11:52:11");
                dialog.setCancelable(true);
                dialog.setNegativeButton("取消",null);
                dialog.show();
                break;
            case R.id.menu_top_update:
                UpDateDictionary();
                break;
        }
        return true;
    }

    public static String ReadTextFromSDcard() {
        InputStreamReader inputStreamReader;
        try {
            File file = new File("/data/data/com.zzzcy.chouxianghua/dictionary.json");
//            Context.getFilesDir().getPath();
            InputStream instream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(instream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStreamReader.close();
            bufferedReader.close();
            String resultString = stringBuilder.toString();
            Dictionary = resultString;
            return resultString;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.i("MyTaps","UnsupportedEncodingException");
            Dictionary ="";
            return "";
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.i("123123","IOException");
            Dictionary ="";
            return "";
        }
    }

    private String Translator(String InPut) {
        try{
            JSONArray jsonArray = new JSONArray(Dictionary);
            Log.d("123123",jsonArray.toString() );
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String raw = jsonObject.optString("raw", null);
//                Log.i("123123",raw);
                String trans = jsonObject.optString("trans", null);
                InPut = InPut.replace(raw,trans);
            }
            return InPut;
        } catch (Exception e) {
            e.printStackTrace();
            return InPut;
        }
    }

    private void UpDateDictionary(){
        new download().execute("http://116.62.147.56:8085/directory.json");
        Log.v("MyTaps",Dictionary);
        Toast.makeText(this, "更新完成", Toast.LENGTH_SHORT).show();
        Log.i("MyTaps","更新完成");
    }

    private static class download extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute(){
            Log.i("MyTaps","准备下载");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.i("MyTaps","开始下载");
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
            if(aBoolean){
                ReadTextFromSDcard();
                Log.i("MyTaps","重载字典");
            }
            else{
                Log.e("MyTaps","未重载字典");
            }
    }
    }
};