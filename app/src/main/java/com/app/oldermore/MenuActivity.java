package com.app.oldermore;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.app.oldermore.database.DatabaseActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class MenuActivity extends Activity {
    private Button btnProfile, btnHealth, btnPost, btnFavorite,
            btnMsgCall, btnEmergency, btnNontifiction, btnPhotoRetouch, btnBoard,
            btnKnowledge, btnManual, btnSetting, btnEmerCall, btnWhere, btnLogout;
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    private DatabaseActivity myDb = new DatabaseActivity(this);
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

        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnHealth = (Button) findViewById(R.id.btnHealth);
        btnMsgCall = (Button) findViewById(R.id.btnMsgCall);
        btnFavorite = (Button) findViewById(R.id.btnFavorite);
        btnEmergency = (Button) findViewById(R.id.btnEmergency);
        btnPost = (Button) findViewById(R.id.btnPost);
        btnNontifiction = (Button) findViewById(R.id.btnNontifiction);
        btnPhotoRetouch = (Button) findViewById(R.id.btnPhotoRetouch);
        btnBoard = (Button) findViewById(R.id.btnBoard);
        btnKnowledge = (Button) findViewById(R.id.btnKnowledge);
        btnManual = (Button) findViewById(R.id.btnManual);
        btnSetting = (Button) findViewById(R.id.btnSetting);

        btnEmerCall = (Button) findViewById(R.id.btnEmerCall);
        btnWhere = (Button) findViewById(R.id.btnWhere);

        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.DeleleLogin();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });

        btnEmerCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:0899999999"));

                if (ActivityCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

        btnWhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(getBaseContext(), MapActivity.class);
              /*  Intent i = new Intent(getBaseContext(), ChatActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);*/
            }
        });

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
