package com.davish.ieeeadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Events> {
    public EventAdapter(Context context, List<Events> object){
        super(context,0, object);
    }

    TextView titleTextView;



    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_mission,parent,false);
        }


         titleTextView = (TextView) convertView.findViewById(R.id.mission_title);

        final Events mission = getItem(position);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditEvent.class);
                i.putExtra("Hash", mission.getHash());
                i.putExtra("Soc", mission.getSoc());
                v.getContext().startActivity(i);
            }
        });

        titleTextView.setText(mission.getTitle());



        return convertView;
    }

}
