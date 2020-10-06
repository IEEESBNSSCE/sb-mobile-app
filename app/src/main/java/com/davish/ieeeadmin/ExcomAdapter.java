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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ExcomAdapter extends ArrayAdapter<Excom> {
    public ExcomAdapter(Context context, List<Excom> object){
        super(context,0, object);
    }


    TextView nameTextView;
    ImageView imageView;
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_mission_excom,parent,false);
        }

        nameTextView = (TextView) convertView.findViewById(R.id.name);

        TextView posTextView = (TextView) convertView.findViewById(R.id.pos);

        imageView = convertView.findViewById(R.id.img);

        final Excom mission = getItem(position);

        nameTextView.setText(mission.getName());
        posTextView.setText(mission.getPosition());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), EditExcom.class);
                i.putExtra("Hash", mission.getHash());
                v.getContext().startActivity(i);
            }
        });


        Bitmap bmImg = null;
        URL myfileurl = null;
        try {
            myfileurl = new URL(mission.getImageUrl());
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

}
