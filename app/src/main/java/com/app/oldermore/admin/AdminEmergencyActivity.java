package com.app.oldermore.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import android.widget.TextView;

import com.app.oldermore.MenuActivity;
import com.app.oldermore.R;
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


public class AdminEmergencyActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    //private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> ArrListEmergecy = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private Button btnAdd, btnMainMenu;
    private ListView listFriend;
    private String[] namePhotoSplite;
    private String mCurrentPhotoPath, strURLUpload, strImgProfile;
    private static final int SELECT_PICTURE = 1;
    private ImageButton btnImageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
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
        listFriend = (ListView) findViewById(R.id.txtSearch);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddEmergency(false, 0);
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

        LoadData();

        final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);
        listFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                try {
                    //int emId = Integer.parseInt(ArrListEmergecy.get(position).get("emergency_id"));
                    DialogAddEmergency(true, position);
                } catch (Exception e) {
                    // When Error
                    MessageDialog(e.getMessage());
                }
            }
        });

        listFriend.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String emergency_id = ArrListEmergecy.get(position).get("emergency_id");
                String emergency_name = ArrListEmergecy.get(position).get("emergency_name");

                viewDetail.setTitle("คุณต้องการลบผู้ติดต่อฉุกเฉินนี้?");
                viewDetail.setMessage("ชื่อ : " + emergency_name)
                        .setCancelable(false)
                        .setPositiveButton(
                                "ยืนยัน",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        DeleteEmergency(emergency_id);
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
    }

    private void LoadData() {
        String url = getString(R.string.url) + "getEmergency.php";

        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));

        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            ArrListEmergecy.clear();
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("emergency_id", c.getString("emergency_id"));
                    map.put("emergency_name", c.getString("emergency_name"));
                    map.put("emergency_mobile", c.getString("emergency_mobile"));
                    map.put("emergency_image", c.getString("emergency_image"));
                    map.put("user_id", c.getString("user_id"));
                    ArrListEmergecy.add(map);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        listFriend.setAdapter(new ImageAdapter(this, ArrListEmergecy));
    }

    private void DeleteEmergency(String emergency_id) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "deleteEmergency.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("emergency_id", emergency_id));
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

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        public ImageAdapter(AdminEmergencyActivity c, ArrayList<HashMap<String, String>> list) {
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
                imageView.setImageBitmap(loadBitmap(getString(R.string.url_images) + MyArr.get(position).get("emergency_image")));
            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            // ColPosition
            TextView txtPosition = (TextView) convertView.findViewById(R.id.ColName);
            txtPosition.setPadding(10, 0, 0, 0);
            txtPosition.setText("ชื่อ : " + MyArr.get(position).get("emergency_name"));

            // ColPicname
            TextView txtPicName = (TextView) convertView.findViewById(R.id.ColMobile);
            txtPicName.setPadding(50, 0, 0, 0);
            txtPicName.setText("เบอร์โทร : " + MyArr.get(position).get("emergency_mobile"));

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
                Log.e(TAG, "Could not close stream", e);
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

    /*****
     * Get Image Resource from URL (End)
     *****/

    private void DialogAddEmergency(Boolean editMode, int position) {
        View dialogBoxView = View.inflate(this, R.layout.dialog_add_emergency, null);
        strImgProfile = "";
        final Button btnSave = (Button) dialogBoxView.findViewById(R.id.btnAdd);
        btnImageProfile = (ImageButton) dialogBoxView.findViewById(R.id.btnImageProfile);
        final EditText txtName = (EditText) dialogBoxView.findViewById(R.id.txtName);
        final EditText txtMobile = (EditText) dialogBoxView.findViewById(R.id.txtMobile);
        final String[] name = {""};
        final String[] mobile = {""};
        String emergency_id = "";
        //String user_id = "";

        if (editMode) {
            emergency_id = ArrListEmergecy.get(position).get("emergency_id");
            name[0] = ArrListEmergecy.get(position).get("emergency_name");
            mobile[0] = ArrListEmergecy.get(position).get("emergency_mobile");
            //user_id = MyArrList.get(0).get("user_id");

            String photo_url_str = getString(R.string.url_images);
            if (!"".equals(ArrListEmergecy.get(position).get("emergency_image")) && ArrListEmergecy.get(position).get("emergency_image") != null) {
                photo_url_str += ArrListEmergecy.get(position).get("emergency_image");
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
        } else {
            emergency_id = "";
            name[0] = txtName.getText().toString().trim();
            mobile[0] = txtMobile.getText().toString().trim();
            //user_id = MyArrList.get(0).get("user_id");
        }


        final String finalEmergency_id = emergency_id;
        //final String finalUser_id = user_id;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name[0] = txtName.getText().toString().trim();
                mobile[0] = txtMobile.getText().toString().trim();

                if (!"".equals(name[0]) && !"".equals(mobile[0])) {
                    // *** Upload file to Server
                    boolean status = uploadFiletoServer(mCurrentPhotoPath, strURLUpload);
                    if (status) {
                        namePhotoSplite = mCurrentPhotoPath.split("/");
                        strImgProfile = namePhotoSplite[namePhotoSplite.length - 1];
                    }
                    SaveData(finalEmergency_id, name[0], mobile[0]);

                    LoadData();
                } else {
                    MessageDialog("กรุณาใส่ข้อมูลให้ครบถ้วน");
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

    private void SaveData(String emergency_id, String member_name, String member_mobile) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "saveEmergency.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("emergency_id", emergency_id));
        params.add(new BasicNameValuePair("user_id", MyArrList.get(0).get("user_id")));
        params.add(new BasicNameValuePair("emergency_name", member_name));
        params.add(new BasicNameValuePair("emergency_mobile", member_mobile));
        params.add(new BasicNameValuePair("emergency_image", strImgProfile));
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

    private void setImage() {
        Bitmap b = BitmapFactory.decodeFile(mCurrentPhotoPath);
        btnImageProfile.setImageBitmap(Bitmap.createScaledBitmap(b, 80, 80, false));
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
/*    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Profile Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }*/
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

}
