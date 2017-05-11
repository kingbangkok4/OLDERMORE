package com.app.oldermore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    private DatabaseActivity myDb = new DatabaseActivity(this);
    private ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> MyArrProfile = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> MyArrEmergency = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> map;
    private Http http = new Http();
    private ImageButton btnImageProfile, ImageEmergency1, ImageEmergency2, ImageEmergency3;
    private EditText txtName, txtMobile;
    private TextView lblName, txtNameEmergency1, txtNameEmergency2, txtNameEmergency3;
    private Button btnSave, btnMainMenu;
    private String mCurrentPhotoPath, strURLUpload, strImgProfile;
    private static final int SELECT_PICTURE = 1;
    private String[] namePhotoSplite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
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

        btnImageProfile = (ImageButton) findViewById(R.id.btnImageProfile);
        ImageEmergency1 = (ImageButton) findViewById(R.id.ImageEmergency1);
        ImageEmergency2 = (ImageButton) findViewById(R.id.ImageEmergency2);
        ImageEmergency3 = (ImageButton) findViewById(R.id.ImageEmergency3);
        txtName = (EditText) findViewById(R.id.txtName);
        txtMobile = (EditText) findViewById(R.id.txtMobile);
        lblName = (TextView) findViewById(R.id.lblName);
        btnSave = (Button) findViewById(R.id.btnAdd);
        btnMainMenu = (Button) findViewById(R.id.btnMainMenu);
        txtNameEmergency1 = (TextView) findViewById(R.id.txtNameEmergency1);
        txtNameEmergency2 = (TextView) findViewById(R.id.txtNameEmergency2);
        txtNameEmergency3 = (TextView) findViewById(R.id.txtNameEmergency3);

        LoadData();
        GetCommon();

        if (MyArrEmergency.size() > 0) {
            DisableEmergency();
            for (int i = 0; i < MyArrEmergency.size(); i++) {
                ShowEmergencyPhoto(MyArrEmergency.get(i).get("emergency_image"), MyArrEmergency.get(i).get("emergency_name"), i);
            }
        } else {
            DisableEmergency();
        }

        ImageEmergency1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyArrEmergency.get(0).get("emergency_mobile") != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+MyArrEmergency.get(0).get("emergency_mobile")));
                    if (ActivityCompat.checkSelfPermission(getBaseContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

                }
            }
        });
        ImageEmergency2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyArrEmergency.get(1).get("emergency_mobile") != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+MyArrEmergency.get(0).get("emergency_mobile")));
                    if (ActivityCompat.checkSelfPermission(getBaseContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

                }
            }
        });
        ImageEmergency3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyArrEmergency.get(2).get("emergency_mobile") != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+MyArrEmergency.get(0).get("emergency_mobile")));
                    if (ActivityCompat.checkSelfPermission(getBaseContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

                }
            }
        });

        btnImageProfile.setOnClickListener(new View.OnClickListener() {
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
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strImgProfile = "";
                if (!"".equals(txtName.getText().toString().trim()) && !"".equals(txtMobile.getText().toString().trim())) {
                    // *** Upload file to Server
                    boolean status = uploadFiletoServer(mCurrentPhotoPath, strURLUpload);
                    if (status) {
                        namePhotoSplite = mCurrentPhotoPath.split("/");
                        strImgProfile = namePhotoSplite[namePhotoSplite.length - 1];
                    }
                    UpdateProfile();
                } else {
                    MessageDialog("กรุณาใส่ข้อมูลให้ครบถ้วน!");
                }
            }
        });
        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MenuActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
    }

    private void DisableEmergency() {
        ImageEmergency1.setEnabled(false);
        txtNameEmergency1.setEnabled(false);
        ImageEmergency2.setEnabled(false);
        txtNameEmergency2.setEnabled(false);
        ImageEmergency3.setEnabled(false);
        txtNameEmergency3.setEnabled(false);
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
                //} else {
                   // MessageDialog(error);
               // }
                LoadDataEmergency();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
    }

    private void LoadDataEmergency() {
        String url = getString(R.string.url) + "getEmergency.php";

        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));

        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            MyArrEmergency.clear();
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("emergency_id", c.getString("emergency_id"));
                    map.put("emergency_name", c.getString("emergency_name"));
                    map.put("emergency_mobile", c.getString("emergency_mobile"));
                    map.put("emergency_image", c.getString("emergency_image"));
                    map.put("user_id", c.getString("user_id"));
                    MyArrEmergency.add(map);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void UpdateProfile() {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "updateProfile.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("member_name", txtName.getText().toString().trim()));
        params.add(new BasicNameValuePair("member_mobile", txtMobile.getText().toString().trim()));
        params.add(new BasicNameValuePair("user_image", strImgProfile));
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

    private void ShowProfile() {
        lblName.setText(MyArrProfile.get(0).get("member_name"));
        txtName.setText(MyArrProfile.get(0).get("member_name"));
        txtMobile.setText(MyArrProfile.get(0).get("member_mobile"));
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

    private void ShowEmergencyPhoto(String photo, String name, int index) {

        String photo_url_str = getString(R.string.url_images);
        if (!"".equals(photo) && photo != null) {
            photo_url_str += photo;
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
        switch (index) {
            case 0:
                ImageEmergency1.setEnabled(true);
                txtNameEmergency1.setEnabled(true);
                ImageEmergency1.setImageBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
                txtNameEmergency1.setText(name);
                break;
            case 1:
                ImageEmergency2.setEnabled(true);
                txtNameEmergency2.setEnabled(true);
                ImageEmergency2.setImageBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
                txtNameEmergency2.setText(name);
                break;
            case 2:
                ImageEmergency3.setEnabled(true);
                txtNameEmergency3.setEnabled(true);
                ImageEmergency3.setImageBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
                txtNameEmergency3.setText(name);
                break;
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

    private void setImage() {
        if(mCurrentPhotoPath != null) {
            Bitmap b = BitmapFactory.decodeFile(mCurrentPhotoPath);
            btnImageProfile.setImageBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
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

    private void GetCommon() {
        SettingModel ret = new SettingModel();
        ret = GetSettingValue();
        RelativeLayout bgElement = (RelativeLayout) findViewById(R.id.container);

        bgElement.setBackgroundColor(Color.parseColor(ret.getBgColor()));
        txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        txtMobile.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        lblName.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        txtNameEmergency1.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        txtNameEmergency2.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
        txtNameEmergency3.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
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
            else if(ret.getBgColor() == null || ret.getFontSize() == 0) {
                ret.setFontSize(20);
                ret.setBgColor("#ffffff");
            }
        }catch (Exception ex){

        }
        return ret;
    }
}
