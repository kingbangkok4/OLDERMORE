package com.app.oldermore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import com.app.oldermore.database.DatabaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IntroActivity extends Activity {
    private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb.openDatabase();
        setContentView(R.layout.activity_intro);
        LinearLayout llProgress = (LinearLayout) findViewById(R.id.ll_progress);
        try {
            // give your gif image name here(example.gif).
            GIFView gif = new GIFView(this, "file:///android_asset/loading.gif");
            llProgress.addView(gif);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyArrList = myDb.CheckLogin();
                if (MyArrList != null) {
                    if (MyArrList.size() > 0) {
                        Intent i = new Intent(IntroActivity.this, MenuActivity.class);
                        i.putExtra("MyArrList", MyArrList);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(IntroActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, 5000);
    }
}
