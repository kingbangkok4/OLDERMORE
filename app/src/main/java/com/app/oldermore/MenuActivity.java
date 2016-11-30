package com.app.oldermore;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class MenuActivity extends Activity{
    private Button btnProfile, btnHealth, btnPost, btnFavorite,
            btnMsgCall, btnEmergency, btnNontifiction, btnPhotoRetouch, btnBoard,
            btnKnowledge, btnManual, btnSetting;
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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

        btnProfile = (Button)findViewById(R.id.btnProfile);
        btnHealth = (Button)findViewById(R.id.btnHealth);
        btnMsgCall = (Button)findViewById(R.id.btnMsgCall);
        btnFavorite = (Button)findViewById(R.id.btnFavorite);
        btnEmergency = (Button)findViewById(R.id.btnEmergency);
        btnPost = (Button)findViewById(R.id.btnPost);
        btnNontifiction = (Button)findViewById(R.id.btnNontifiction);
        btnPhotoRetouch = (Button)findViewById(R.id.btnPhotoRetouch);
        btnBoard = (Button)findViewById(R.id.btnBoard);
        btnKnowledge = (Button)findViewById(R.id.btnKnowledge);
        btnManual = (Button)findViewById(R.id.btnManual);
        btnSetting = (Button)findViewById(R.id.btnSetting);


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ProfileActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), HealthActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnMsgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MsgCallActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), FavoriteActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), EmergencyActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), PostActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnNontifiction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), NontificationActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnPhotoRetouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), PhotoRetouchActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), BoardActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnKnowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), KnowledgeActivity.class);
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
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SettingActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
    }
}
