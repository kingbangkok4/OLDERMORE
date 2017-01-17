package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.oldermore.http.Http;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends Activity {
    private Http http = new Http();
    private static final String TAG = "ChatActivity";
    private ChatArrayAdapter adp;
    private ListView list;
    private EditText chatText;
    private Button btnSend, btnBack;
    Intent intent;
    private boolean side = false;
    private String lastChatId = "0";
    private String friendId = "";
    private String friendName = "";
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ArrListChat = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            friendId = (String) extras.getString("friendId");
            friendName = (String) extras.getString("friendName");
            if (tmpMyArrList != null) {
                MyArrList = tmpMyArrList;
            }
        }

        Intent i = getIntent();
        setContentView(R.layout.activity_chat);
        TextView txtNameFriend = (TextView) findViewById(R.id.txtNameFriend);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnBack = (Button) findViewById(R.id.btnBack);
        list = (ListView) findViewById(R.id.listview);

        txtNameFriend.setText("เพื่อน : "+friendName);
        adp = new ChatArrayAdapter(getApplicationContext(), R.layout.chat);
        list.setAdapter(adp);
        chatText = (EditText) findViewById(R.id.chat_text);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode ==
                        KeyEvent.KEYCODE_ENTER)) {
                    String strMessage = chatText.getText().toString().trim();
                    if (!"".equals(strMessage)) {
                        SaveMessage(chatText.getText().toString().trim());
                    }
                }
                return false;
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String strMessage = chatText.getText().toString().trim();
                if (!"".equals(strMessage)) {
                    SaveMessage(strMessage);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MsgCallActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });

        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setAdapter(adp);
        adp.registerDataSetObserver(new DataSetObserver() {
            public void OnChanged() {
                super.onChanged();
                list.setSelection(adp.getCount() - 1);
            }
        });

        Loop();
        //Test Loop
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Loop();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
        //End Test Loop
    }

    private void SaveMessage(String strMessage) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "saveChat.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("friend_id", friendId));
        params.add(new BasicNameValuePair("message", strMessage));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
                    chatText.setText("");
                    Loop();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
    }


    public void Loop() {
        String url = getString(R.string.url) + "getChat.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("friend_id", friendId));
        params.add(new BasicNameValuePair("last_id", lastChatId));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            ArrListChat.clear();
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id", c.getString("id"));
                    map.put("user_id", c.getString("user_id"));
                    map.put("message", c.getString("message"));
                    map.put("user_image", c.getString("user_image").equals("") ? "user.png" : c.getString("user_image"));
                    map.put("member_name", c.getString("member_name"));
                    map.put("member_mobile", c.getString("member_mobile"));
                    ArrListChat.add(map);
                }
                lastChatId = ArrListChat.get(ArrListChat.size() - 1).get("id");
                for (int i = 0; i < ArrListChat.size(); i++) {
                    if (MyArrList.get(0).get("user_id").equals(ArrListChat.get(i).get("user_id"))) {
                        side = false;
                    } else {
                        side = true;
                    }
                    sendChatMessage(ArrListChat.get(i).get("message"), ArrListChat.get(i).get("user_image"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //chatText.setText("Hello Chat");
    }

    private boolean sendChatMessage(String strMessage, String image) {
        // adp.add(new ChatMessage(side, chatText.getText().toString()));
        adp.add(new ChatMessage(side, strMessage.trim(), getString(R.string.url_images) + image));
        chatText.setText("");
        side = !side;
        return true;
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