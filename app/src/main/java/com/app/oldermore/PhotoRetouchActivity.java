package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.app.oldermore.common.SettingModel;
import com.app.oldermore.database.DatabaseActivity;
import com.app.oldermore.http.Http;

import java.util.ArrayList;
import java.util.HashMap;


public class PhotoRetouchActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private Button btnMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_retouch);
        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Bundle extras = getIntent().getExtras();
        // เช็คว่ามีค่าที่ส่งมาจากหน้าอื่นหรือไม่ถ้ามีจะไม่เท่ากับ null
        if (extras != null) {
            tmpMyArrList = (ArrayList<HashMap<String, String>>) extras
                    .getSerializable("MyArrList");
            if (tmpMyArrList != null) {
                MyArrList = tmpMyArrList;
            }
        }

        btnMainMenu = (Button) findViewById(R.id.btnMainMenu);

        GetCommon();

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MenuActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });

    }

    private void GetCommon() {
        SettingModel ret = new SettingModel();
        ret = GetSettingValue();
        RelativeLayout bgElement = (RelativeLayout) findViewById(R.id.container);

        bgElement.setBackgroundColor(Color.parseColor(ret.getBgColor()));
        btnMainMenu.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
    }

    private SettingModel GetSettingValue(){
        SettingModel ret = new SettingModel();
        try {
            ret = myDb.GetSetting();
            if(ret == null){
                ret.setFontSize(20);
                ret.setBgColor("#ffffff");
            }
            else if(ret.getBgColor() == null || ret.getFontSize() == 0) {
                ret.setFontSize(20);
                ret.setBgColor("#ffffff");
            }
        }catch (Exception ex){

        }
        return ret;
    }

    private void MessageDialog(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
