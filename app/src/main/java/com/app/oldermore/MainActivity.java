package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.oldermore.admin.AdminMainActivity;
import com.app.oldermore.database.DatabaseActivity;
import com.app.oldermore.http.Http;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
    private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Button btnLogin, btnRegister;
    private EditText txtUsername, txtPassword;
    private TextView lblFogotPassword;
    private Http http = new Http();
    /*    private String strUsername = "";
        private String strPassword = "";
        private String strType = "";*/
    private String strError = "Unknow Status!";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        txtUsername = (EditText) findViewById(R.id.txtUserName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        lblFogotPassword = (TextView) findViewById(R.id.lblFogotPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  // TestศToast.makeText(getApplicationContext(), strError, Toast.LENGTH_SHORT).show();
                map = new HashMap<String, String>();
                map.put("username", "staff");
                map.put("password", "1234");
                map.put("type", "Staff");
                MyArrList.add(map);
                Intent ii = new Intent(MainActivity.this, ServiceActivity.class);
                ii.putExtra("MyArrList", MyArrList);
                startActivity(ii);*/
                //************ end test
                Boolean status = OnLogin();
                if (status) {
                    Toast.makeText(getApplicationContext(), strError, Toast.LENGTH_SHORT).show();
                    // Insert ข้อมูลลง SQLite Database เพื่อครั้งต่อไปไม่ต้อง Login ใหม่
                    myDb.InsertLogin(MyArrList.get(0).get("user_id"),
                            MyArrList.get(0).get("username"),
                            MyArrList.get(0).get("password"),
                            MyArrList.get(0).get("user_image"),
                            MyArrList.get(0).get("type"));

                    if(("ADMIN").equals(MyArrList.get(0).get("type"))){
                        Intent i = new Intent(getBaseContext(), AdminMainActivity.class);
                        i.putExtra("MyArrList", MyArrList);
                        startActivity(i);
                    }else {
                        Intent i = new Intent(getBaseContext(), MenuActivity.class);
                        i.putExtra("MyArrList", MyArrList);
                        startActivity(i);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), strError, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
        lblFogotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFogotPassword();
            }
        });
    }

    private void DialogFogotPassword() {
        View dialogBoxView = View.inflate(this, R.layout.dialog_get_password, null);
        final Button btnGetPassword = (Button) dialogBoxView.findViewById(R.id.btnGetPassword);
        final EditText txtUsername = (EditText) dialogBoxView.findViewById(R.id.txtUsername);

        btnGetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPassword = "";
                String status = "0";
                String error = "";
                String url = getString(R.string.url) + "getPassword.php";
                // Paste Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", txtUsername.getText()
                        .toString().trim()));
                try {
                    JSONArray data = new JSONArray(http.getJSONUrl(url, params));
                    if (data.length() > 0) {
                        JSONObject c = data.getJSONObject(0);
                        status = c.getString("status");
                        error = c.getString("error");
                        if ("1".equals(status)) {
                            strPassword = c.getString("password");
                        }
                    }
                    if ("1".equals(status)) {
                        MessageDialog("รหัสผ่าน : "+ strPassword);
                    } else {
                        MessageDialog(error);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    MessageDialog(e.getMessage());
                }

            }
        });

        AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        builderInOut.setTitle("ลืมรหัสผ่าน");
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

    private boolean OnLogin() {
/*        final AlertDialog.Builder ad = new AlertDialog.Builder(this);*/
        Boolean ststusLogin = false;
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "getUser.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", txtUsername.getText()
                .toString().trim()));
        params.add(new BasicNameValuePair("password", txtPassword.getText()
                .toString().trim()));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                if ("1".equals(status)) {
                    map = new HashMap<String, String>();
                    map.put("user_id", c.getString("user_id"));
                    map.put("username", c.getString("username"));
                    map.put("password", c.getString("password"));
                    map.put("user_image", c.getString("user_image"));
                    map.put("type", c.getString("type"));
                    MyArrList.add(map);

                    //myDb.InsertLogin(MyArrList.get(0).get("username"), MyArrList.get(0).get("password"), MyArrList.get(0).get("type"));
                }
            }

            if ("1".equals(status)) {
                ststusLogin = true;
                strError = "รหัสผ่านถูกต้อง...";
            } else {
                ststusLogin = false;
                strError = "รหัสผ่านไม่ถูกต้อง";
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ststusLogin = false;
            strError = e.getMessage();
        }
        return ststusLogin;
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

