package com.app.oldermore.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.oldermore.R;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class AdminManualActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChoose, buttonUpload;
    private Button buttonChoosePic, buttonUploadPic;
    private TextView textView, textView1;

    private static final int SELECT_VIDEO = 3;
    private static final int SELECT_PICTURE = 1;

    private String selectedPath;
    private String type = "", strPic = "", strVideo = "";
    private Http http = new Http();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manual);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonChoosePic = (Button) findViewById(R.id.buttonChoosePic);
        buttonUploadPic = (Button) findViewById(R.id.buttonUploadPic);

        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);
        //textViewResponse = (TextView) findViewById(R.id.textViewResponse);

        textView.setText("");
        textView1.setText("");

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

        buttonChoosePic.setOnClickListener(this);
        buttonUploadPic.setOnClickListener(this);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video "), SELECT_VIDEO);
    }

    private void choosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                Uri selectedVideoUri = data.getData();
                selectedPath = getPath(selectedVideoUri);
                textView.setText(selectedPath);
                String[] nameSplite = selectedPath.split("/");
                strVideo = nameSplite[nameSplite.length - 1];
            }else if (requestCode == SELECT_PICTURE) {
                System.out.println("SELECT_PICTURE");
                Uri selectedPicUri = data.getData();
                selectedPath = getPathPic(selectedPicUri);
                textView1.setText(selectedPath);
                String[] nameSplite = selectedPath.split("/");
                strPic = nameSplite[nameSplite.length - 1];
            }
        }

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    public String getPathPic(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(AdminManualActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                //textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                //textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
                //MessageDialog(s);
                UpdateManual("video");
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.upLoad2Server(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private void uploadPic(String strSDPath, String strUrlServer) {
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
                //return false;
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
                //MessageDialog(resMessage);
                UpdateManual("pic");
            }

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();


        } catch (Exception ex) {
            // Exception handling
            MessageDialog(ex.getMessage());
        }
    }

    private void UpdateManual(String type) {
        String status = "0";
        String error = "";
        String url = getString(R.string.url) + "updateManual.php";
        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if("video".equals(type.trim())){
            params.add(new BasicNameValuePair("image", ""));
            params.add(new BasicNameValuePair("video", strVideo));
        }else {
            params.add(new BasicNameValuePair("image", strPic));
            params.add(new BasicNameValuePair("video", ""));
        }

        try {
            JSONArray data = new JSONArray(http.getJSONUrl(url, params));
            if (data.length() > 0) {
                JSONObject c = data.getJSONObject(0);
                status = c.getString("status");
                error = c.getString("error");
                if ("1".equals(status)) {
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

    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            type = "video";
            chooseVideo();
        }
        if (v == buttonUpload) {
            uploadVideo();
        }
        if (v == buttonChoosePic) {
            type = "pic";
            choosePic();
        }
        if (v == buttonUploadPic) {
            uploadPic(selectedPath, getString(R.string.url_upload));
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