package com.app.oldermore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.oldermore.http.Http;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FavoriteActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ArrListFriend = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ArrListFavorite = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private Button btnMainMenu, btnAdd, btnSearch;
    private EditText txtName;
    private ListView listFriend;
    private ListView listFavorite;
    private String friendName, friendId, nameSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
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
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        txtName = (EditText) findViewById(R.id.txtName);
        listFriend = (ListView) findViewById(R.id.txtSearch);

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MenuActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialodAddFriend();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadData();
            }
        });

        LoadData();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        listFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String friendId = ArrListFriend.get(position).get("friend_id");
                String friendName = ArrListFriend.get(position).get("member_name");
                Intent i = new Intent(getBaseContext(), ChatActivity.class);
                i.putExtra("MyArrList", MyArrList);
                i.putExtra("friendId",friendId);
                i.putExtra("friendName",friendName);
                startActivity(i);
            }
        });
        listFriend.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                friendId = ArrListFriend.get(position).get("id");
                builder.setTitle("คุณต้องการลบคนพิเศษ?");
                builder.setMessage(ArrListFriend.get(position).get("member_name"))
                        .setCancelable(false)
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SaveAddFavorite("0");
                            }
                        })
                        .setNegativeButton("ไม่ใช่",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                return false;
            }
        });
    }

    private void LoadData() {
        String url = getString(R.string.url) + "getFavorite.php";

        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("search", txtName.getText().toString().trim()));

        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            ArrListFriend.clear();
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id", c.getString("id"));
                    map.put("friend_id", c.getString("friend_id"));
                    map.put("user_image", c.getString("user_image").equals("")?"user.png":c.getString("user_image"));
                    map.put("member_name", c.getString("member_name"));
                    map.put("member_mobile", c.getString("member_mobile"));
                    ArrListFriend.add(map);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        listFriend.setAdapter(new ImageAdapter(this, ArrListFriend));
    }

    private void DialodAddFriend() {
        View dialogBoxView = View.inflate(this, R.layout.dialog_add_friend, null);
        friendName = "";
        final Button btnAFSerch = (Button) dialogBoxView.findViewById(R.id.btnSearch);
        final EditText txtAFSearch = (EditText) dialogBoxView.findViewById(R.id.txtSearch);
        final TextView txtAFFriend = (TextView) dialogBoxView.findViewById(R.id.txtFriend);
        listFavorite = (ListView) dialogBoxView.findViewById(R.id.listFriend);
        txtAFFriend.setText("");

        LoadDataFriend();

        btnAFSerch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameSearch = txtAFSearch.getText().toString().trim();
                LoadDataFriend();
            }
        });

        listFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtAFFriend.setText(ArrListFavorite.get(position).get("member_name"));
                friendId = ArrListFavorite.get(position).get("id");
            }
        });

        AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        builderInOut.setTitle("เพิ่มเพื่อน");
        builderInOut.setMessage("")
                .setView(dialogBoxView)
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        friendName = txtAFFriend.getText().toString().trim();
                        if(!"".equals(friendName) && !"".equals(friendId)){
                            SaveAddFavorite("1");
                        }else {
                            MessageDialog("กรุณาเลือกเพื่อนที่ต้องการเพิ่ม");
                        }
                    }
                })
                .setNegativeButton("ปิด",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
    }

    private void SaveAddFavorite(String favorite) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "saveFavorite.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("friend_id", friendId));
        params.add(new BasicNameValuePair("favorite", favorite));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
                    LoadData();
                    MessageDialog(error);
                } else {
                    MessageDialog(error);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
    }

    private void LoadDataFriend() {
        String url = getString(R.string.url) + "getFriend.php";

        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("search", nameSearch));

        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            ArrListFavorite.clear();
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id", c.getString("id"));
                    map.put("friend_id", c.getString("friend_id"));
                    map.put("user_image", c.getString("user_image").equals("")?"user.png":c.getString("user_image"));
                    map.put("member_name", c.getString("member_name"));
                    map.put("member_mobile", c.getString("member_mobile"));
                    ArrListFavorite.add(map);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        listFavorite.setAdapter(new FavoriteActivity.ImageAdapter(this, ArrListFavorite));
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        public ImageAdapter(FavoriteActivity c, ArrayList<HashMap<String, String>> list) {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_emergency_column, null);
            }

            // ColImage
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ColImgPath);
            imageView.getLayoutParams().height = 100;
            imageView.getLayoutParams().width = 100;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try {
                imageView.setImageBitmap(loadBitmap(getString(R.string.url_images) + MyArr.get(position).get("user_image")));
            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            // ColPosition
            TextView txtPosition = (TextView) convertView.findViewById(R.id.ColName);
            txtPosition.setPadding(10, 0, 0, 0);
            txtPosition.setText("ชื่อ : " + MyArr.get(position).get("member_name"));

            // ColPicname
            TextView txtPicName = (TextView) convertView.findViewById(R.id.ColMobile);
            txtPicName.setPadding(50, 0, 0, 0);
            txtPicName.setText("เบอร์โทร : " + MyArr.get(position).get("member_mobile"));

            return convertView;

        }

    }

    /*****
     * Get Image Resource from URL (Start)
     *****/
    private static final String TAG = "ERROR";
    private static final int IO_BUFFER_SIZE = 4 * 1024;

    public static Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            final byte[] data = dataStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 1;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (IOException e) {
            Log.e(TAG, "Could not load Bitmap from: " + url);
        } finally {
            closeStream(in);
            closeStream(out);
        }

        return bitmap;
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e(TAG, "Could not close stream", e);
            }
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
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
}
