package com.app.oldermore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/8/2017.
 */

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage>{
    private TextView chatText;//, chatProfile;
    private ImageView chatProfile;
    private List<ChatMessage> MessageList = new ArrayList<ChatMessage>();
    private LinearLayout layout;
    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public void add(ChatMessage object) {
        // TODO Auto-generated method stub

        MessageList.add(object);
        super.add(object);
    }

    public int getCount()
    {
        return this.MessageList.size();
    }

    public ChatMessage getItem(int index){

        return this.MessageList.get(index);
    }

    public View getView(int position,View ConvertView, ViewGroup parent){

        View v = ConvertView;
        if(v==null){

            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =inflater.inflate(R.layout.chat, parent,false);

        }

        layout = (LinearLayout)v.findViewById(R.id.Message1);
        ChatMessage messageobj = getItem(position);
        chatText =(TextView)v.findViewById(R.id.SingleMessage);
        chatProfile = (ImageView) v.findViewById(R.id.SingleProfile);
        chatText.setText(messageobj.message);
        try {
            //chatProfile.setImageBitmap(loadBitmap(messageobj.image));
            chatProfile.setImageBitmap(Bitmap.createScaledBitmap(loadBitmap(messageobj.image), 80, 80, false));
        } catch (Exception e) {
            // When Error
            chatProfile.setImageResource(android.R.drawable.ic_menu_report_image);
        }
        //chatProfile.setBackgroundResource(messageobj.left ? R.drawable.bubble_a :R.drawable.bubble_b);
        layout.setGravity(messageobj.left?Gravity.LEFT:Gravity.RIGHT);
        return v;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
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
}
