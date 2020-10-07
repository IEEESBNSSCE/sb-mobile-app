package com.davish.ieeeadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SocietyAdapter extends ArrayAdapter<Society> {
    public SocietyAdapter(Context context, List<Society> object){
        super(context,0, object);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_mission_society,parent,false);
        }

        final TextView nameTextView = convertView.findViewById(R.id.name);

        ImageView imageView = convertView.findViewById(R.id.img);

        final Society mission = getItem(position);



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), EditSociety.class);
                i.putExtra("Hash", mission.getHash());
                v.getContext().startActivity(i);
            }
        });



        nameTextView.setText(mission.getName());

        Bitmap bmImg = null;
        URL myfileurl = null;
        try {
            myfileurl = new URL(mission.getImageurl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            HttpURLConnection conn = (HttpURLConnection) myfileurl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            int length = conn.getContentLength();
            if (length > 0) {
                int[] bitmapData = new int[length];
                byte[] bitmapData2 = new byte[length];
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bmImg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}
