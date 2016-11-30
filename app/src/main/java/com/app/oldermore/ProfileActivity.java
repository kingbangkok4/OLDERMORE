package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;

import com.app.oldermore.http.Http;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends Activity{
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> MyArrProfile = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private  Button btnImageProfile;
    private EditText txtName, txtMobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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

        btnImageProfile = (Button)findViewById(R.id.btnImageProfile);
        txtName  = (EditText)findViewById(R.id.txtName);
        txtMobile = (EditText)findViewById(R.id.txtMobile);

        LoadData();

    }

    private void LoadData() {
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
                if ("1".equals(status)) {
                    map = new HashMap<String, String>();
                    map.put("member_id", c.getString("member_id"));
                    map.put("member_name", c.getString("member_name"));
                    map.put("member_mobile", c.getString("member_mobile"));
                    map.put("member_address", c.getString("member_address"));
                    map.put("member_email", c.getString("member_email"));
                    MyArrProfile.add(map);

                    ShowProfile();
                }else {
                    MessageDialog(error);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
    }

    private void ShowProfile() {
        txtName.setText(MyArrProfile.get(0).get("member_name"));
        txtMobile.setText(MyArrProfile.get(0).get("member_mobile"));
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
