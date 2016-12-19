package com.app.oldermore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class HealthActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> MyArrHealthList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> MyArrEmergency = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private ImageButton ImageEmergency1, ImageEmergency2, ImageEmergency3;
    private TextView lblName, txtNameEmergency1, txtNameEmergency2, txtNameEmergency3;
    private Button btnSave, btnMainMenu;
    private EditText txtConDisease, txtDrugAllergy, txtDoctor, txtDoctorMobile, txtHotel, txtHotelMobile;
    private String conDisease, drugAllergy, doctor, doctorMobile, hotel, hotelMobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
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

        btnSave = (Button)findViewById(R.id.btnSave);
        btnMainMenu = (Button)findViewById(R.id.btnMainMenu);

        ImageEmergency1 = (ImageButton) findViewById(R.id.ImageEmergency1);
        ImageEmergency2 = (ImageButton) findViewById(R.id.ImageEmergency2);
        ImageEmergency3 = (ImageButton) findViewById(R.id.ImageEmergency3);

        txtNameEmergency1 = (TextView) findViewById(R.id.txtNameEmergency1);
        txtNameEmergency2 = (TextView) findViewById(R.id.txtNameEmergency2);
        txtNameEmergency3 = (TextView) findViewById(R.id.txtNameEmergency3);

        txtConDisease = (EditText)findViewById(R.id.txtConDisease);
        txtDrugAllergy = (EditText)findViewById(R.id.txtDrugAllergy);
        txtDoctor = (EditText)findViewById(R.id.txtDoctor);
        txtDoctorMobile = (EditText)findViewById(R.id.txtDoctorMobile);
        txtHotel = (EditText)findViewById(R.id.txtHotel);
        txtHotelMobile = (EditText)findViewById(R.id.txtHotelMobile);

        LoadData();

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // conDisease, drugAllergy, doctor, doctorMobile, hotel, hotelMobile;
                conDisease = txtConDisease.getText().toString().trim();
                drugAllergy = txtDrugAllergy.getText().toString().trim();
                doctor = txtDoctor.getText().toString().trim();
                doctorMobile = txtDoctorMobile.getText().toString().trim();
                hotel = txtHotel.getText().toString().trim();
                hotelMobile = txtHotelMobile.getText().toString().trim();
              /*  if (!"".equals(name)&&!"".equals(conDisease)&&!"".equals(drugAllergy)&&!"".equals(doctor)
                        &&!"".equals(doctorMobile)&&!"".equals(hotel)&&!"".equals(hotelMobile))
                {*/
                    SaveData();
                /*} else {
                    MessageDialog("กรุณาใส่ข้อมูลให้ครบถ้วน!");
                }*/
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

    private void SaveData() {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "saveHealth.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("health_id", MyArrHealthList.get(0).get("health_id")));
        params.add(new BasicNameValuePair("con_disease", txtConDisease.getText().toString().trim()));
        params.add(new BasicNameValuePair("drug_allergy", txtDrugAllergy.getText().toString().trim()));
        params.add(new BasicNameValuePair("doctor", txtDoctor.getText().toString().trim()));
        params.add(new BasicNameValuePair("doctor_mobile", txtDoctorMobile.getText().toString().trim()));
        params.add(new BasicNameValuePair("hotel", txtHotel.getText().toString().trim()));
        params.add(new BasicNameValuePair("hotel_mobile", txtHotelMobile.getText().toString().trim()));

        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");

                MessageDialog(error);

                LoadData();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
    }

    private void LoadData() {
        MyArrHealthList = new ArrayList<HashMap<String, String>>();
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "getHealth.php";
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
                    map.put("health_id", c.getString("health_id"));
                    map.put("con_disease", c.getString("con_disease"));
                    map.put("drug_allergy", c.getString("drug_allergy"));
                    map.put("doctor", c.getString("doctor"));
                    map.put("doctor_mobile", c.getString("doctor_mobile"));
                    map.put("hotel", c.getString("hotel"));
                    map.put("hotel_mobile", c.getString("hotel_mobile"));
                    map.put("user_id", c.getString("user_id"));
                    MyArrHealthList.add(map);

                    ShowHealth();
                //} else {
                    //MessageDialog(error);
               // }
                LoadDataEmergency();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
    }

    private void ShowHealth() {
        txtConDisease.setText(MyArrHealthList.get(0).get("con_disease"));
        txtDrugAllergy.setText(MyArrHealthList.get(0).get("drug_allergy"));
        txtDoctor.setText(MyArrHealthList.get(0).get("doctor"));
        txtDoctorMobile.setText(MyArrHealthList.get(0).get("doctor_mobile"));
        txtHotel.setText(MyArrHealthList.get(0).get("hotel"));
        txtHotelMobile.setText(MyArrHealthList.get(0).get("hotel_mobile"));
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

    private void DisableEmergency() {
        ImageEmergency1.setEnabled(false);
        txtNameEmergency1.setEnabled(false);
        ImageEmergency2.setEnabled(false);
        txtNameEmergency2.setEnabled(false);
        ImageEmergency3.setEnabled(false);
        txtNameEmergency3.setEnabled(false);
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
}
