package com.app.oldermore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.oldermore.common.SettingModel;
import com.app.oldermore.database.DatabaseActivity;
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
    private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> MyArrHealthList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> MyArrEmergency = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private ImageButton ImageEmergency1, ImageEmergency2, ImageEmergency3;
    private TextView lblName, txtNameEmergency1, txtNameEmergency2, txtNameEmergency3;
    private ListView listHelth;
    private Button btnAdd, btnMainMenu;
    // private EditText txtConDisease, txtDrugAllergy, txtDoctor, txtDoctorMobile, txtHotel, txtHotelMobile;
    private String conDisease, drugAllergy, doctor, doctorMobile, hotel, hotelMobile, healthId;

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

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnMainMenu = (Button) findViewById(R.id.btnMainMenu);

        ImageEmergency1 = (ImageButton) findViewById(R.id.ImageEmergency1);
        ImageEmergency2 = (ImageButton) findViewById(R.id.ImageEmergency2);
        ImageEmergency3 = (ImageButton) findViewById(R.id.ImageEmergency3);

        txtNameEmergency1 = (TextView) findViewById(R.id.txtNameEmergency1);
        txtNameEmergency2 = (TextView) findViewById(R.id.txtNameEmergency2);
        txtNameEmergency3 = (TextView) findViewById(R.id.txtNameEmergency3);

        listHelth = (ListView) findViewById(R.id.listHelth);

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

        final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
        listHelth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                try {
                    //int emId = Integer.parseInt(ArrListFreind.get(position).get("emergency_id"));
                    conDisease = MyArrHealthList.get(position).get("con_disease");
                    drugAllergy = MyArrHealthList.get(position).get("drug_allergy");
                    doctor = MyArrHealthList.get(position).get("doctor");
                    doctorMobile = MyArrHealthList.get(position).get("doctor_mobile");
                    hotel = MyArrHealthList.get(position).get("hotel");
                    hotelMobile = MyArrHealthList.get(position).get("hotel_mobile");

                    DialogShowHealth(MyArrHealthList.get(position).get("health_id"));
                } catch (Exception e) {
                    // When Error
                    MessageDialog(e.getMessage());
                }
            }
        });
        listHelth.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String health_id = MyArrHealthList.get(position).get("health_id");
                String con_disease = MyArrHealthList.get(position).get("con_disease");

                viewDetail.setTitle("คุณต้องการลบ?");
                viewDetail.setMessage("โรคประจำตัว : " + con_disease)
                        .setCancelable(false)
                        .setPositiveButton(
                                "ยืนยัน",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        DeleteHelth(health_id);
                                        dialog.dismiss();
                                        LoadData();
                                    }
                                })
                        .setNegativeButton(
                                "ยกเลิก",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = viewDetail.create();
                alert.show();

                return false;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddHelth();
            }
        });

        ImageEmergency1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyArrEmergency.get(0).get("emergency_mobile") != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + MyArrEmergency.get(0).get("emergency_mobile")));
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
                    callIntent.setData(Uri.parse("tel:" + MyArrEmergency.get(0).get("emergency_mobile")));
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
                    callIntent.setData(Uri.parse("tel:" + MyArrEmergency.get(0).get("emergency_mobile")));
                    if (ActivityCompat.checkSelfPermission(getBaseContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

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

    private void DialogAddHelth() {
        View dialogBoxView = View.inflate(this, R.layout.dialog_health, null);
        final EditText txtConDisease = (EditText)dialogBoxView.findViewById(R.id.txtConDisease);
        final EditText txtDrugAllergy = (EditText)dialogBoxView.findViewById(R.id.txtDrugAllergy);
        final EditText txtDoctor = (EditText)dialogBoxView.findViewById(R.id.txtDoctor);
        final EditText txtDoctorMobile = (EditText)dialogBoxView.findViewById(R.id.txtDoctorMobile);
        final EditText txtHotel = (EditText)dialogBoxView.findViewById(R.id.txtHotel);
        final EditText txtHotelMobile = (EditText)dialogBoxView.findViewById(R.id.txtHotelMobile);

        AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        builderInOut.setTitle("เพิ่มข้อมูลสุขภาพ");
        builderInOut.setMessage("")
                .setView(dialogBoxView)
                .setCancelable(false)
               .setPositiveButton("บันทึก", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        healthId = "";
                        conDisease = txtConDisease.getText().toString().trim();
                        drugAllergy = txtDrugAllergy.getText().toString().trim();
                        doctor = txtDoctor.getText().toString().trim();
                        doctorMobile = txtDoctorMobile.getText().toString().trim();
                        hotel = txtHotel.getText().toString().trim();
                        hotelMobile = txtHotelMobile.getText().toString().trim();

                        SaveData();

                        dialog.cancel();
                    }
                })
                .setNegativeButton("ปิด",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
    }

    private void DialogShowHealth(String health_id) {
        View dialogBoxView = View.inflate(this, R.layout.dialog_health, null);
        final EditText txtConDisease = (EditText)dialogBoxView.findViewById(R.id.txtConDisease);
        final EditText txtDrugAllergy = (EditText)dialogBoxView.findViewById(R.id.txtDrugAllergy);
        final EditText txtDoctor = (EditText)dialogBoxView.findViewById(R.id.txtDoctor);
        final EditText txtDoctorMobile = (EditText)dialogBoxView.findViewById(R.id.txtDoctorMobile);
        final EditText txtHotel = (EditText)dialogBoxView.findViewById(R.id.txtHotel);
        final EditText txtHotelMobile = (EditText)dialogBoxView.findViewById(R.id.txtHotelMobile);

        healthId = health_id;
        txtConDisease.setText(conDisease);
        txtDrugAllergy.setText(drugAllergy);
        txtDoctor.setText(doctor);
        txtDoctorMobile.setText(doctorMobile);
        txtHotel.setText(hotel);
        txtHotelMobile.setText(hotelMobile);

        AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        builderInOut.setTitle("แก้ไขข้อมูลสุขภาพ");
        builderInOut.setMessage("")
                .setView(dialogBoxView)
                .setCancelable(false)
                .setPositiveButton("บันทึก", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        conDisease = txtConDisease.getText().toString().trim();
                        drugAllergy = txtDrugAllergy.getText().toString().trim();
                        doctor = txtDoctor.getText().toString().trim();
                        doctorMobile = txtDoctorMobile.getText().toString().trim();
                        hotel = txtHotel.getText().toString().trim();
                        hotelMobile = txtHotelMobile.getText().toString().trim();

                        SaveData();

                        dialog.cancel();
                    }
                })
                .setNegativeButton("ปิด",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
    }

    private void SaveData() {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "saveHealth.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("health_id", healthId));
        params.add(new BasicNameValuePair("con_disease", conDisease));
        params.add(new BasicNameValuePair("drug_allergy", drugAllergy));
        params.add(new BasicNameValuePair("doctor", doctor));
        params.add(new BasicNameValuePair("doctor_mobile", doctorMobile));
        params.add(new BasicNameValuePair("hotel", hotel));
        params.add(new BasicNameValuePair("hotel_mobile", hotelMobile));

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

    private void DeleteHelth(String health_id) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "deleteHelth.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("health_id", health_id));
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
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

    private void LoadData() {
        String url = getString(R.string.url) + "getHealth.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));

        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            MyArrHealthList.clear();
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
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
                }
            }
            SimpleAdapter sAdap;
            sAdap = new SimpleAdapter(getBaseContext(), MyArrHealthList, R.layout.activity_one_column,
                    new String[] {"con_disease"}, new int[] {R.id.ColName});
            listHelth.setAdapter(sAdap);

            LoadDataEmergency();
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

    private void GetCommon() {
        SettingModel ret = new SettingModel();
        ret = GetSettingValue();
        RelativeLayout bgElement = (RelativeLayout) findViewById(R.id.container);

        bgElement.setBackgroundColor(Color.parseColor(ret.getBgColor()));
        btnAdd.setTextSize(TypedValue.COMPLEX_UNIT_SP, ret.getFontSize());
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
        }catch (Exception ex){

        }
        return ret;
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
