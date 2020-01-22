package com.zzzcy.chouxianghua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class LauncherActivity extends AppCompatActivity {
    public static final String  FIRST_LAUNCHER="first_launcher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;


        if(isFirstLauncher()){//为true第二次启动  因为第一次启动的时候我们会把FIRST_LAUNCHER的值变为true
            intent=new Intent(LauncherActivity.this,MainActivity.class);
//            Toast.makeText(this, "非第一次启动", Toast.LENGTH_SHORT).show();
        }else{
            intent=new Intent(LauncherActivity.this,FirstLauncherActivity.class);
            setFirstLauncherBoolean();
//            Toast.makeText(this, "第一次启动", Toast.LENGTH_SHORT).show();
        }
        startActivity(intent);
        finish();
    }

    public boolean isFirstLauncher(){
        SharedPreferences sp=getSharedPreferences("config",Context.MODE_PRIVATE);
        return sp.getBoolean(FIRST_LAUNCHER,false);
    }

    public void setFirstLauncherBoolean(){
        SharedPreferences sp=getSharedPreferences("config",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putBoolean(LauncherActivity.FIRST_LAUNCHER,true);
        edit.apply();
    }
}
