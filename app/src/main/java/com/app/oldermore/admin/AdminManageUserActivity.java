package com.app.oldermore.admin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.oldermore.R;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminManageUserActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    // ArrayList<HashMap<String, String>> ArrListFC = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ArrListFriend = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private Button btnMainMenu, btnSearch;
    private String[] arrFriendChat = {"เพื่อน", "พูดคุย"};
    private ListView listView;
    private EditText txtSearch;
    private String friendName = "";
    private String friendUserId = "", strImgProfile = "";
    private String nameSearch = "";
    private ListView listHelth;
    ArrayList<HashMap<String, String>> MyArrHealthList = new ArrayList<HashMap<String, String>>();
    private String conDisease, drugAllergy, doctor, doctorMobile, hotel, hotelMobile, healthId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_user);
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

        //btnMainMenu, btnSearch, btnFriend, btnChat
        btnMainMenu = (Button) findViewById(R.id.btnMainMenu);
        btnSearch = (Button) findViewById(R.id.btnAdd);
        listView = (ListView) findViewById(R.id.listView);
        txtSearch = (EditText) findViewById(R.id.txtSearch);

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AdminMainActivity.class);
                i.putExtra("MyArrList", MyArrList);
                startActivity(i);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDataFriend();
            }
        });

        LoadDataFriend();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                try {
                    //int emId = Integer.parseInt(ArrListFreind.get(position).get("emergency_id"));
                    DialogUpdateProfile(position);
                } catch (Exception e) {
                    // When Error
                    MessageDialog(e.getMessage());
                }

               /* String friendId = ArrListFC.get(position).get("friend_id");
                String friendName = ArrListFC.get(position).get("member_name");
                Intent i = new Intent(getBaseContext(), ChatActivity.class);
                i.putExtra("MyArrList", MyArrList);
                i.putExtra("friendId", friendId);
                i.putExtra("friendName", friendName);
                startActivity(i);*/
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                friendUserId = ArrListFriend.get(position).get("user_id");
                builder.setTitle("คุณต้องการลบผู้ใช้งานนี้?");
                builder.setMessage(ArrListFriend.get(position).get("member_name"))
                        .setCancelable(false)
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DeleteUser();
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

    private void DialogUpdateProfile(int position) {
        View dialogBoxView = View.inflate(this, R.layout.dialog_add_emergency, null);
        String strImgProfile = "";
        final Button btnSave = (Button) dialogBoxView.findViewById(R.id.btnAdd);
        final Button btnCall = (Button) dialogBoxView.findViewById(R.id.btnCall);
        final Button btnHealth = (Button) dialogBoxView.findViewById(R.id.btnHealth);
        final Button btnFavorite = (Button) dialogBoxView.findViewById(R.id.btnFavorite);
        final ImageButton btnImageProfile = (ImageButton) dialogBoxView.findViewById(R.id.btnImageProfile);
        final EditText txtName = (EditText) dialogBoxView.findViewById(R.id.txtName);
        final EditText txtMobile = (EditText) dialogBoxView.findViewById(R.id.txtMobile);
        final String[] name = {""};
        final String[] mobile = {""};
        //String member_id = "";
        String user_id = "";

        user_id = ArrListFriend.get(position).get("user_id");
        name[0] = ArrListFriend.get(position).get("member_name");
        mobile[0] = ArrListFriend.get(position).get("member_mobile");
        final String police = ArrListFriend.get(position).get("police");

        String photo_url_str = getString(R.string.url_images);
        if (!"".equals(ArrListFriend.get(position).get("user_image")) && ArrListFriend.get(position).get("user_image") != null) {
            photo_url_str += ArrListFriend.get(position).get("user_image");
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
        btnImageProfile.setImageBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));

        txtName.setText(name[0]);
        txtMobile.setText(mobile[0]);

        final String finalUserId = user_id;
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(txtMobile.getText().toString().trim())) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + txtMobile.getText().toString().trim()));

                    if (ActivityCompat.checkSelfPermission(getBaseContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                }
            }
        });
        //final String finalUser_id = user_id;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name[0] = txtName.getText().toString().trim();
                mobile[0] = txtMobile.getText().toString().trim();

                if (!"".equals(name[0]) && !"".equals(mobile[0])) {
                    SaveData(finalUserId, name[0], mobile[0], police);

                    LoadDataFriend();
                } else {
                    MessageDialog("กรุณาใส่ข้อมูลให้ครบถ้วน");
                }
            }
        });
        final String finalUser_id = user_id;
        btnHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHealth(finalUser_id);
            }
        });
        AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        builderInOut.setTitle("เพิ่ม - แก้ไข ผู้ติดต่อฉุกเฉิน");
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

    private void DialogHealth(final String user_id) {
        View dialogBoxView = View.inflate(this, R.layout.activity_admin_health, null);
        listHelth = (ListView) dialogBoxView.findViewById(R.id.listHelth);
        Button btnAdd = (Button) dialogBoxView.findViewById(R.id.btnAdd);
        LoadHelthData(user_id);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddHelth();
            }
        });

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
        final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
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
                                        LoadHelthData(user_id);
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

        AlertDialog.Builder builderInOut = new AlertDialog.Builder(this);
        builderInOut.setTitle("เพิ่ม - แก้ไข ข้อมูลสุขภาพ");
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

    private void SaveData(String user_id, String member_name, String member_mobile, String police) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "updateProfile.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", user_id));
        params.add(new BasicNameValuePair("member_name", member_name));
        params.add(new BasicNameValuePair("member_mobile", member_mobile));
        params.add(new BasicNameValuePair("user_image", strImgProfile));
        params.add(new BasicNameValuePair("police", police));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
                    LoadDataFriend();
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

    private void DeleteUser() {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "deleteUser.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("friend_id", friendUserId));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
                    LoadDataFriend();
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
        String url = getString(R.string.url) + "getMemberAll.php";
        nameSearch = txtSearch.getText().toString().trim();
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("search", nameSearch));

        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            ArrListFriend.clear();
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("member_id", c.getString("member_id"));
                    map.put("user_id", c.getString("user_id"));
                    map.put("user_image", c.getString("user_image").equals("") ? "user.png" : c.getString("user_image"));
                    map.put("member_name", c.getString("member_name"));
                    map.put("member_mobile", c.getString("member_mobile"));
                    map.put("police", c.getString("police"));
                    ArrListFriend.add(map);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setAdapter(new AdminManageUserActivity.ImageAdapter(this, ArrListFriend));
    }

    protected void LoadHelthData(String user_id) {
        String url = getString(R.string.url) + "getHealth.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", user_id));

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

            //LoadDataEmergency();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
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

                        SaveHelpData();

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

                        SaveHelpData();

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

    private void DeleteHelth(String health_id) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "deleteHelth.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("health_id", health_id));
        params.add(new BasicNameValuePair("user_id", MyArrHealthList.get(0).get("user_id")));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
                    LoadHelthData(MyArrHealthList.get(0).get("user_id"));
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

    private void SaveHelpData() {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "saveHealth.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrHealthList.get(0).get("user_id")));
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

                LoadHelthData(MyArrHealthList.get(0).get("user_id"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageDialog(e.getMessage());
        }
    }
    //==============================================================================================

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        public ImageAdapter(AdminManageUserActivity c, ArrayList<HashMap<String, String>> list) {
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

    private static void copy(InputStream in, BufferedOutputStream out) throws IOException {
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
