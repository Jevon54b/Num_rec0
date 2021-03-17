package com.example.num_rec1;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetCtrl{
    static AssetManager am;
    static InputStream is;
    static BufferedReader br;
    public static void setAssetManager(AssetManager am){
        AssetCtrl.am=am;
    }

    public void openFile(String path){
        try {
            is = am.open(path);
            br=new BufferedReader(new InputStreamReader(is));
        }
        catch (IOException e){
            return ;
        }
    }

    public String readLine(){
        try {
            return br.readLine();//空会return null
        }
        catch (IOException e){
            return null;
        }
    }
}
