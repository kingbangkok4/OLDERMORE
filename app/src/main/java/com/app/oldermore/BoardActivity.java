package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.app.oldermore.http.Http;

import java.util.ArrayList;
import java.util.HashMap;


public class BoardActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private WebView webView;
    Button btnMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
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

        webView = (WebView) findViewById(R.id.webView1);
        btnMainMenu = (Button) findViewById(R.id.btnMainMenu);

/*        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.google.co.th");*/

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(getString(R.string.str_url_webboard)+"?user="+MyArrList.get(0).get("username"));

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MenuActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
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
