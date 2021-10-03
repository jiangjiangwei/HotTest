package com.night.myapplication;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class MyApplicaiton extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //hot fix
        String path =  Environment.getExternalStorageDirectory().getPath() + File.separator + "hotfixed.dex";
        Log.e("MyApplicaiton", "fixedpath===" + path);
        //storage/emulated/0/hotfixed.dex
        HotFix.getInstance().hotInstallDexByPath(this, path);
//        ReflectUtils.findField(getClassLoader(),"pathList");
    }
}
