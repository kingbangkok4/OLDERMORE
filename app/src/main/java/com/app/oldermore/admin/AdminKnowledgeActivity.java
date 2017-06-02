package com.app.oldermore.admin;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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


public class AdminKnowledgeActivity extends Activity {
    private Double sumTotal = 0.00;
    private StringBuilder strDetailService = new StringBuilder();
    Button btnMainMenu, btnSearch, btnAdd;
    EditText txtKnowledge;
    ListView listKnowledge;
    /*  private FeedListAdapter listAdapter;
      private List<FeedItem> feedItems;*/
    private DatabaseActivity myDb = new DatabaseActivity(this);
    ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpMyArrList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> knowledgeArrList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    private Http http = new Http();
    private ImageButton btnImageProfile;
    private String mCurrentPhotoPath, strURLUpload, strImgProfile;
    private String[] namePhotoSplite;
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
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
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        txtKnowledge = (EditText) findViewById(R.id.txtKnowledge);
        listKnowledge = (ListView) findViewById(R.id.listKnowledge);

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
                LoadData();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddKnowledge(false, 0);
            }
        });

        LoadData();

        final AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);

        listKnowledge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogAddKnowledge(true, position);
            }
        });
        listKnowledge.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String knowledgeId = knowledgeArrList.get(position).get("id");
                String content = knowledgeArrList.get(position).get("status");

                viewDetail.setTitle("คุณต้องการลบสาระน่ารู้นี้?");
                viewDetail.setMessage(" " + content.substring(0, 20))
                        .setCancelable(false)
                        .setPositiveButton(
                                "ยืนยัน",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        DeleteKnowledge(knowledgeId);
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

    private void DeleteKnowledge(String knowledgeId) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "deleteKnowledge.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", knowledgeId));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
                    //LoadData();
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

    private void DialogAddKnowledge(boolean editMode, int position) {
        View dialogBoxView = View.inflate(this, R.layout.dialog_add_knowledge, null);
        strImgProfile = "";
        String id = null;
        final String[] content = new String[1];
        String image = "";
        final Button btnSave = (Button) dialogBoxView.findViewById(R.id.btnAdd);
        final EditText txtMobile = (EditText) dialogBoxView.findViewById(R.id.txtContent);
        btnImageProfile = (ImageButton) dialogBoxView.findViewById(R.id.btnImageProfile);

        if (editMode) {
            id = knowledgeArrList.get(position).get("id");
            image = knowledgeArrList.get(position).get("image");
            content[0] = knowledgeArrList.get(position).get("status");

            String photo_url_str = getString(R.string.url_images);
            if (!"".equals(knowledgeArrList.get(position).get("image")) && knowledgeArrList.get(position).get("image") != null) {
                photo_url_str += knowledgeArrList.get(position).get("image");
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
            btnImageProfile.setImageBitmap(Bitmap.createScaledBitmap(b, 250, 100, false));

            txtMobile.setText(content[0]);
        } else {
            id = "";
        }
        final String finalId = id;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = uploadFiletoServer(mCurrentPhotoPath, strURLUpload);
                if (status) {
                    namePhotoSplite = mCurrentPhotoPath.split("/");
                    strImgProfile = namePhotoSplite[namePhotoSplite.length - 1];
                    content[0] = txtMobile.getText().toString().trim();
                    if (!"".equals(strImgProfile) && !"".equals(txtMobile.getText().toString().trim())) {
                        SaveData(finalId, strImgProfile, content[0]);
                        LoadData();
                    } else {
                        MessageDialog("กรุณาใส่ข้อมูลให้ครบถ้วน!!");
                    }
                } else {
                    MessageDialog("กรุณาเลือกรูป!!");
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
        builderInOut.setTitle("เพิ่มเบอร์ติดต่อฉุกเฉิน");
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

    private void LoadData() {
        String url = getString(R.string.url) + "getKnowledge.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("status", txtKnowledge.getText().toString().trim()));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            knowledgeArrList.clear();
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id", c.getString("id"));
                    map.put("name", c.getString("name"));
                    map.put("image", c.getString("image"));
                    map.put("status", c.getString("status"));
                    map.put("timeStamp", c.getString("timeStamp"));
                    knowledgeArrList.add(map);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        listKnowledge.setAdapter(new ImageAdapter(this, knowledgeArrList));
    }

    private void SaveData(String id, String image, String content) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "saveKnowledge.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("image", image));
        params.add(new BasicNameValuePair("status", content));
        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
                    //LoadData();
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

        public ImageAdapter(AdminKnowledgeActivity c, ArrayList<HashMap<String, String>> list) {
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
                imageView.setImageBitmap(loadBitmap(getString(R.string.url_images) + MyArr.get(position).get("image")));
            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            // ColPosition
            TextView txtPosition = (TextView) convertView.findViewById(R.id.ColName);
            txtPosition.setPadding(10, 0, 0, 0);
            txtPosition.setText(" " + MyArr.get(position).get("status"));

            // ColPicname
          /*  TextView txtPicName = (TextView) convertView.findViewById(R.id.ColMobile);
            txtPicName.setPadding(50, 0, 0, 0);
            txtPicName.setText("เบอร์โทร : " + MyArr.get(position).get("emergency_mobile"));*/

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

    /*****
     * Get Image Resource from URL (End)
     *****/

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
        if (b != null) {
            btnImageProfile.setImageBitmap(Bitmap.createScaledBitmap(b, 250, 100, false));
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
