package com.zzzcy.chouxianghua;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {

    public String Dictionary;

    public EditText renhua;
    public EditText chouxianghua;
    public Toolbar toolbar;
    public FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        renhua = (EditText)findViewById(R.id.renhua);
        chouxianghua = (EditText)findViewById(R.id.chouxianghua);
        toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);

        setSupportActionBar(toolbar);

        Dictionary = ReadTextFromSDcard();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    chouxianghua.setText(Translator(renhua.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                dialog.setMessage("其实根本没有孙笑川，或者说人人都是孙笑川。\n\nVersion:20200121161123");
                dialog.setCancelable(true);
                dialog.setNegativeButton("取消",null);
                dialog.show();
                break;
            case R.id.menu_top_update:
                Toast.makeText(this, "你再骂！", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private String ReadTextFromSDcard() {
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(getAssets().open("dictionary.json"), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStreamReader.close();
            bufferedReader.close();
            String resultString = stringBuilder.toString();
//            Log.i("TAG", resultString);
            return resultString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String Translator(String InPut) throws JSONException {
        try{
            JSONArray jsonArray = new JSONArray(Dictionary);
            Log.d("TAG",jsonArray.toString() );
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String raw = jsonObject.optString("raw", null);
                String trans = jsonObject.optString("trans", null);
                InPut = InPut.replace(raw,trans);
            }
            return InPut;
        } catch (Exception e) {
            e.printStackTrace();
            return InPut;
        }
    }

};