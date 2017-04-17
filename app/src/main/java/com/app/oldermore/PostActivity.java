package com.app.oldermore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.oldermore.common.SettingModel;
import com.app.oldermore.database.DatabaseActivity;
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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.androidhive.listviewfeed.adapter.FeedListAdapter;
import info.androidhive.listviewfeed.data.FeedItem;


public class PostActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    private DatabaseActivity myDb = new DatabaseActivity(this);
    private ArrayList<HashMap<String, String>> MyArrProfile = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> ArrListImageApp = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private ImageButton btnImageProfile;
    private TextView lblName;
    private Button btnMainMenu, btnPost, btnAppImage, btnImage, btnPostImage;
    private EditText txtPost;

    private static final String TAG = MainFeedActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    //private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
    private String URL_FEED = "";

    private String mCurrentPhotoPath, strURLUpload, strImgPost;
    private static final int SELECT_PICTURE = 1;
    private String[] namePhotoSplite;

    @SuppressLint("NewApi")
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

        btnPostImage = (Button) findViewById(R.id.btnPostImage);

        btnImageProfile = (ImageButton) findViewById(R.id.btnImageProfile);
        lblName = (TextView) findViewById(R.id.lblName);
        listView = (ListView) findViewById(R.id.listPost);

        txtPost = (EditText) findViewById(R.id.txtPost);

        URL_FEED = getString(R.string.url) + "getPost.php";

        LoadData();
        LoadDataPost();
        parseJsonFeed();
        LoadDataImageApp();
        GetCommon();

        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
       /* getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));*/

       /* // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }*/

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
                if (!"".equals(txtPost.getText().toString().trim())) {
                    // *** Upload file to Server
                    boolean status = uploadFiletoServer(mCurrentPhotoPath, strURLUpload);
                    if (status) {
                        namePhotoSplite = mCurrentPhotoPath.split("/");
                        strImgPost = namePhotoSplite[namePhotoSplite.length - 1];
                    }
                    SavePost();
                } else {
                    MessageDialog("กรุณาใส่ข้อมูลให้ครบถ้วน!");
                }
            }
        });
        btnAppImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogImageApp();
            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // *** Upload file to Server
                strURLUpload = getString(R.string.url_upload);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        SELECT_PICTURE);
            }
        });
    }

    private void LoadDataImageApp() {
        for (int i = 1; i <= 65; i++) {
            map = new HashMap<String, String>();
            map.put("image", "a" + i + ".jpg");
            ArrListImageApp.add(map);
        }
    }

    private void DialogImageApp() {
        View dialogBoxView = View.inflate(this, R.layout.dialog_image_app, null);
        final String[] strTmpImage = {""};
        final ListView listImageApp = (ListView) dialogBoxView.findViewById(R.id.listImageApp);
        final Button btnSelectImage = (Button) dialogBoxView.findViewById(R.id.btnSelectImage);
        final AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        listImageApp.setAdapter(new ImageAppAdapter(this, ArrListImageApp));

        listImageApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                try {
                    strTmpImage[0] = ArrListImageApp.get(position).get("image");

                    String photo_url_str = getString(R.string.url_images) + "/images_app/" + strTmpImage[0];
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
                        btnSelectImage.setBackgroundDrawable(new BitmapDrawable(getResources(), b));
                    } else {
                        btnSelectImage.setBackground(new BitmapDrawable(getResources(), b));
                    }
                } catch (Exception e) {
                    // When Error
                    MessageDialog(e.getMessage());
                }
            }
        });


        builderInOut.setTitle("คลังรูป");
        builderInOut.setMessage("")
                .setView(dialogBoxView)
                .setCancelable(false)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            strImgPost = "/images_app/"+strTmpImage[0];

                            String photo_url_str = getString(R.string.url_images) + "/images_app/" + strTmpImage[0];
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
                                btnPostImage.setBackgroundDrawable(new BitmapDrawable(getResources(), b));
                            } else {
                                btnPostImage.setBackground(new BitmapDrawable(getResources(), b));
                            }
                            dialog.cancel();
                        } catch (Exception e) {
                            // When Error
                            MessageDialog(e.getMessage());
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

    public class ImageAppAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        public ImageAppAdapter(PostActivity c, ArrayList<HashMap<String, String>> list) {
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
                convertView = inflater.inflate(R.layout.activity_image_column, null);
            }

            // ColImage
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ColImgPath);
            imageView.getLayoutParams().height = 250;
            imageView.getLayoutParams().width = 500;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try {
                imageView.setImageBitmap(loadBitmap(getString(R.string.url_images) + "/images_app/" + MyArr.get(position).get("image")));
            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            return convertView;

        }

    }

    private void SavePost() {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "savePost.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("status", txtPost.getText().toString().trim()));
        params.add(new BasicNameValuePair("image", strImgPost));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
                    LoadData();
                    MessageDialog(error);
                    ClearData();

                    LoadData();
                    LoadDataPost();
                    parseJsonFeed();
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
        btnImageProfile.setImageBitmap(Bitmap.createScaledBitmap(b, 100, 100, false));
    }

    private void LoadDataPost() {
        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     */
    /*private void parseJsonFeed(JSONObject response) {*/
    private void parseJsonFeed() {
        try {
            // Paste Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
            //JSONArray feedArray = response.getJSONArray("feed");
            JSONArray feedArray = new JSONArray(http.getJSONUrl(URL_FEED, params));
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : getString(R.string.url_images) + feedObj
                        .getString("image").replace("\\","");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(getString(R.string.url_images) + feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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

    private void ClearData(){
        txtPost.setText("");
        strImgPost = "";
        String photo_url_str = getString(R.string.url_images) + "no.png";
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
            btnPostImage.setBackgroundDrawable(new BitmapDrawable(getResources(), b));
        } else {
            btnPostImage.setBackground(new BitmapDrawable(getResources(), b));
        }
    }

    private void setImage() {
        Bitmap b = BitmapFactory.decodeFile(mCurrentPhotoPath);
        if (android.os.Build.VERSION.SDK_INT < 16) {
            btnPostImage.setBackgroundDrawable(new BitmapDrawable(getResources(), b));
        } else {
            btnPostImage.setBackground(new BitmapDrawable(getResources(), b));
        }

    }

    public static boolean uploadFiletoServer(String strSDPath, String strUrlServer) {

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 10 * 1024 * 1024;
        int resCode = 0;
        String resMessage = "";

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try {
            File file = new File(strSDPath);
            if (!file.exists()) {
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(new File(strSDPath));

            URL url = new URL(strUrlServer);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes(
                    "Content-Disposition: form-data; name=\"filUpload\";filename=\"" + strSDPath + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Response Code and Message
            resCode = conn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int read = 0;
                while ((read = is.read()) != -1) {
                    bos.write(read);
                }
                byte[] result = bos.toByteArray();
                bos.close();

                resMessage = new String(result);

            }

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            return true;

        } catch (Exception ex) {
            // Exception handling
            return false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            mCurrentPhotoPath = getPath(selectedImageUri);
            System.out.println("Image Path : " + mCurrentPhotoPath);
            setImage();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /*****
     * Get Image Resource from URL (Start)
     *****/
    private static final String TAG_IMAGE = "ERROR";
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
            Log.e(TAG_IMAGE, "Could not load Bitmap from: " + url);
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
                android.util.Log.e(TAG_IMAGE, "Could not close stream", e);
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

    private void GetCommon() {
        SettingModel ret = new SettingModel();
        ret = GetSettingValue();
        RelativeLayout bgElement = (RelativeLayout) findViewById(R.id.container);

        bgElement.setBackgroundColor(Color.parseColor(ret.getBgColor()));
        btnPost.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        btnAppImage.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        btnImage.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        btnPostImage.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        lblName.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        txtPost.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        btnMainMenu.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
    }

    private SettingModel GetSettingValue(){
        SettingModel ret = new SettingModel();
        try {
            ret = myDb.GetSetting();
            if(ret == null){
                ret.setFontSize(20);
                ret.setBgColor("#ffffff");
            }
        }catch (Exception ex){

        }
        return ret;
    }

}
