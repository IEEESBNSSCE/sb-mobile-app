package com.davish.ieeeadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RelativeLayout rel_edit = findViewById(R.id.edit);
        RelativeLayout rel_add = findViewById(R.id.add);
        rel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(Home.this, EditViewActivity.class);
                startActivity(I);
            }
        });
        rel_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(Home.this, AddActivity.class);
                startActivity(I);
            }
        });


    }

}
