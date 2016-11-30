package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.oldermore.http.Http;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity {
    private Button btnSubmit;
    private EditText txtUsername, txtPassword, txtRePassword, txtAddress, txtName, txtMobile, txtEmail;
    private String username, password, rePassword, address, name, mobile, email;
    private Http http = new Http();
    private String status = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPass);
        txtRePassword = (EditText) findViewById(R.id.txtRePassword);
        txtName = (EditText) findViewById(R.id.txtName);
        txtMobile = (EditText) findViewById(R.id.txtMobile);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtAddress = (EditText) findViewById(R.id.txtAddress);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
    }

    private void ValidateData() {
        username = txtUsername.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
        rePassword = txtRePassword.getText().toString().trim();
        name = txtName.getText().toString().trim();
        mobile = txtMobile.getText().toString().trim();
        email = txtEmail.getText().toString().trim();
        address = txtAddress.getText().toString().trim();

        if (!"".equals(username)
                && !"".equals(password)
                && !"".equals(rePassword)
                && !"".equals(name)
                && !"".equals(mobile)
                && !"".equals(email)
                && !"".equals(address)
                ) {
            if ((password).equals(rePassword)) {
                String status = this.SaveData();
                this.MessageDialog(status);
            } else {
                MessageDialog("รหัสผ่านไม่ตรงกัน !");
            }
        } else {
            MessageDialog("กรุณากรอกข้อมูลให้ครับถ้วน !");
        }
    }

    private String SaveData() {
        String strStatus = "";
/*        final AlertDialog.Builder ad = new AlertDialog.Builder(this);*/
        String url = "";
        //String url = getString(R.string.url) + "saveMember.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("mobile", mobile));
        params.add(new BasicNameValuePair("address", address));
        try {
            url = getString(R.string.url) + "getUser.php";
            JSONArray data_user = new JSONArray(http.getJSONUrl(url, params));
            if (data_user.length() > 0) {
                JSONObject c1 = data_user.getJSONObject(0);
                status = c1.getString("status");
                if ("0".equals(status)) {
                    url = getString(R.string.url) + "saveUser.php";
                    JSONArray data_save_user = new JSONArray(http.getJSONUrl(url, params));
                    JSONObject cc = data_save_user.getJSONObject(0);
                    String user_id = cc.getString("user_id");
                    params.add(new BasicNameValuePair("user_id", user_id));

                    url = getString(R.string.url) + "saveMember.php";
                    JSONArray data_member = new JSONArray(http.getJSONUrl(url, params));
                    JSONObject c2 = data_member.getJSONObject(0);
                    status = c2.getString("status");
                    strStatus = status;
                }
                else {
                    strStatus = "ผู้ใช้งานนี้มีอยู่ในระบบแล้ว !";
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strStatus;
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
