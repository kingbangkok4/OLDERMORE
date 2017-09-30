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
import android.widget.TextView;

import com.app.oldermore.common.SettingModel;
import com.app.oldermore.database.DatabaseActivity;
import com.app.oldermore.http.Http;

import java.util.ArrayList;
import java.util.HashMap;


public class SettingActivity extends Activity implements View.OnClickListener{
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private Button btnMainMenu, btnFont16, btnFont18, btnFont20, btnFont22, btnFont24,btnManual,
    btnBg1, btnBg2, btnBg3, btnBg4, btnBg0, btnSet;
    private  String bgColor = "#ffffff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
        btnSet = (Button)findViewById(R.id.btnSet);

        btnFont16 = (Button) findViewById(R.id.btnFont16);
        btnFont18 = (Button) findViewById(R.id.btnFont18);
        btnFont20 = (Button) findViewById(R.id.btnFont20);
        btnFont22 = (Button) findViewById(R.id.btnFont22);
        btnFont24 = (Button) findViewById(R.id.btnFont24);

        btnBg0 = (Button) findViewById(R.id.btnBg0);
        btnBg1 = (Button) findViewById(R.id.btnBg1);
        btnBg2 = (Button) findViewById(R.id.btnBg2);
        btnBg3 = (Button) findViewById(R.id.btnBg3);
        btnBg4 = (Button) findViewById(R.id.btnBg4);
        btnManual =(Button) findViewById(R.id.btnManual);

        btnFont16.setOnClickListener(this);
        btnFont18.setOnClickListener(this);
        btnFont20.setOnClickListener(this);
        btnFont22.setOnClickListener(this);
        btnFont24.setOnClickListener(this);
        btnBg0.setOnClickListener(this);
        btnBg1.setOnClickListener(this);
        btnBg2.setOnClickListener(this);
        btnBg3.setOnClickListener(this);
        btnBg4.setOnClickListener(this);

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MenuActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });

        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ManualActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        LoadData();
        GetCommon();

    }

    private void LoadData() {
        SettingModel getModel = new SettingModel();
        getModel = GetSettingValue();
        int size = getModel.getFontSize();
        btnSet.setTextSize(size);
        btnSet.setText(Integer.toString(size));
        btnSet.setBackgroundColor(Color.parseColor(getModel.getBgColor()));

        bgColor = getModel.getBgColor();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFont16:
                setFont(btnFont16.getText().toString().trim());
                break;
            case R.id.btnFont18:
                setFont(btnFont18.getText().toString().trim());
                break;
            case R.id.btnFont20:
                setFont(btnFont20.getText().toString().trim());
                break;
            case R.id.btnFont22:
                setFont(btnFont22.getText().toString().trim());
                break;
            case R.id.btnFont24:
                setFont(btnFont24.getText().toString().trim());
                break;

            case R.id.btnBg0:
                bgColor = "#ffffff";
                setBgColor("#ffffff");
                break;
            case R.id.btnBg1:
                bgColor = "#eee6ff";
                setBgColor("#eee6ff");
                break;
            case R.id.btnBg2:
                bgColor = "#ffe6f2";
                setBgColor("#ffe6f2");
                break;
            case R.id.btnBg3:
                bgColor = "#e6f7ff";
                setBgColor("#e6f7ff");
                break;
            case R.id.btnBg4:
                bgColor = "#eafaea";
                setBgColor("#eafaea");
                break;
        }
    }

    private void setFont(String strFontSize){
        int size = Integer.parseInt(strFontSize);
        btnSet.setTextSize(size);
        btnSet.setText(strFontSize);
        AddSetting();
        GetCommon();
    }
    private void setBgColor(String strBgColor){
        btnSet.setBackgroundColor(Color.parseColor(strBgColor));
        AddSetting();
        GetCommon();
    }

    private void AddSetting(){
        try {
            int size = Integer.parseInt(btnSet.getText().toString().trim());
            SettingModel model = new SettingModel();
            model.setFontSize(size);
            model.setBgColor(bgColor);
            myDb.AddSetting(model);
        }catch(Exception ex){

        }
    }

    private void GetCommon() {
        SettingModel ret = new SettingModel();
        ret = GetSettingValue();
        RelativeLayout bgElement = (RelativeLayout) findViewById(R.id.container);
        TextView textView9 = (TextView)findViewById(R.id.textView9);
        TextView textView17 = (TextView)findViewById(R.id.textView17);

        bgElement.setBackgroundColor(Color.parseColor(ret.getBgColor()));
        btnMainMenu.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        textView9.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        textView17.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
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
