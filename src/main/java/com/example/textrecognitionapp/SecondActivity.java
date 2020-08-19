package com.example.textrecognitionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
private EditText tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tv2=findViewById(R.id.tv2);
       Intent intent=getIntent();
        String a=intent.getStringExtra("data");
        tv2.setText(a);
      //  startActivity(intent);
    }
}
