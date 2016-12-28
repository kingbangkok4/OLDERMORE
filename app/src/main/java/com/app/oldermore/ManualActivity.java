package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.app.oldermore.http.Http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class ManualActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private Button btnMainMenu, btnVideo, btnWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
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
        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnWord = (Button) findViewById(R.id.btnWord);

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MenuActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogVideo();
            }
        });
        btnWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogWord();
            }
        });
    }

    private void DialogVideo() {
        String video = "question_video";
        View dialogBoxView = View.inflate(this, R.layout.dialog_video, null);
        final VideoView myVideoV = (VideoView) dialogBoxView.findViewById(R.id.videoView1);
        myVideoV.setVideoURI(Uri.parse(getString(R.string.str_url_video) + video+".mp4"));
        myVideoV.setMediaController(new MediaController(this));
        myVideoV.start();
        myVideoV.requestFocus();

        AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        builderInOut.setTitle("วิธีใช้ (วีดีโอ)");
        builderInOut.setMessage("")
                .setView(dialogBoxView)
                .setCancelable(false)
       /*         .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })*/
                .setNegativeButton("ปิด",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
    }

    private void DialogWord() {
        View dialogBoxView = View.inflate(this, R.layout.dialog_word, null);
        final Button layout = (Button) dialogBoxView.findViewById(R.id.vDialogWord);
       //messageImage.jpg
        String photo_url_str = getString(R.string.url_images) + "messageImage.jpg";
        URL newurl = null;
        try {
            newurl = new URL(photo_url_str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(android.os.Build.VERSION.SDK_INT < 16) {
            layout.setBackgroundDrawable(new BitmapDrawable(getResources(), b));
        }
        else {
            layout.setBackground(new BitmapDrawable(getResources(),b));
        }

        AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        builderInOut.setTitle("วิธีใช้");
        builderInOut.setMessage("")
                .setView(dialogBoxView)
                .setCancelable(false)
       /*         .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })*/
                .setNegativeButton("ปิด",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
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
