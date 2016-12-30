package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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


public class PostActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    private ArrayList<HashMap<String, String>> MyArrProfile = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private ImageButton btnImageProfile;
    private TextView lblName;
    private Button btnMainMenu, btnPost, btnAppImage, btnImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
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
        btnPost = (Button) findViewById(R.id.btnPost);
        btnAppImage = (Button) findViewById(R.id.btnAppImage);
        btnImage = (Button) findViewById(R.id.btnImage);

        btnImageProfile = (ImageButton) findViewById(R.id.btnImageProfile);
        lblName = (TextView) findViewById(R.id.lblName);

        LoadData();

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MenuActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnAppImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void LoadData() {
        MyArrProfile = new ArrayList<HashMap<String, String>>();
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "getMember.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                //if ("1".equals(status)) {
                map = new HashMap<String, String>();
                map.put("member_id", c.getString("member_id"));
                map.put("member_name", c.getString("member_name"));
                map.put("member_mobile", c.getString("member_mobile"));
                map.put("member_address", c.getString("member_address"));
                map.put("member_email", c.getString("member_email"));
                map.put("user_image", c.getString("user_image"));
                MyArrProfile.add(map);

                ShowProfile();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
    }

    private void ShowProfile() {
        lblName.setText(MyArrProfile.get(0).get("member_name"));
        String photo_url_str = getString(R.string.url_images);
        if (!"".equals(MyArrProfile.get(0).get("user_image")) && MyArrProfile.get(0).get("user_image") != null) {
            photo_url_str += MyArrProfile.get(0).get("user_image");
        } else {
            photo_url_str += "no.png";
        }
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
        btnImageProfile.setImageBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
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
