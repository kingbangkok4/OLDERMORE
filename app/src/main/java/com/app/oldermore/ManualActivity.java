package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.app.oldermore.common.CommonClass;
import com.app.oldermore.common.SettingModel;
import com.app.oldermore.http.Http;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManualActivity extends Activity {
    private CommonClass common = new  CommonClass();
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> MyArrManualList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private Button btnMainMenu, btnVideo, btnWord;
    private String manual_video;
    private String manual_word;
    private RelativeLayout bgElement;

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

        bgElement = (RelativeLayout) findViewById(R.id.container);
        //bgElement.setBackgroundColor(Color.WHITE);
        btnMainMenu = (Button) findViewById(R.id.btnMainMenu);
        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnWord = (Button) findViewById(R.id.btnWord);

        GetCommon();
        LoadData();

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
        String video = manual_video;
        View dialogBoxView = View.inflate(this, R.layout.dialog_video, null);
        final VideoView myVideoV = (VideoView) dialogBoxView.findViewById(R.id.videoView1);
        myVideoV.setVideoURI(Uri.parse(getString(R.string.str_url_video) + video));
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
        String photo_url_str = getString(R.string.url_images) + manual_word;
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
        if (android.os.Build.VERSION.SDK_INT < 16) {
            layout.setBackgroundDrawable(new BitmapDrawable(getResources(), b));
        } else {
            layout.setBackground(new BitmapDrawable(getResources(), b));
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

    private void LoadData() {
        MyArrManualList = new ArrayList<HashMap<String, String>>();
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "getManual.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("xxx", MyArrList.get(0).get("xxx")));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                //if ("1".equals(status)) {
                map = new HashMap<String, String>();
                map.put("manual_id", c.getString("manual_id"));
                map.put("manual_video", c.getString("manual_video"));
                map.put("manual_word", c.getString("manual_word"));
                MyArrManualList.add(map);
            }
            if ("1".equals(status)) {
                manual_video = MyArrManualList.get(0).get("manual_video");
                manual_word = MyArrManualList.get(0).get("manual_word");
            } else {

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
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

    private void GetCommon(){
        SettingModel ret = new SettingModel();
        ret = common.GetSettingValue();
        bgElement.setBackgroundColor(Color.parseColor(ret.getBgColor()));
    }
}
