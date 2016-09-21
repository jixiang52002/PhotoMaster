package com.jixiang52002.photomaster.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jixiang52002.photomaster.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 色光三原色调整
     * @param view
     */
    public void btnPrimaryColor(View view){
        Intent intent=new Intent(this,PrimaryColor.class);
        startActivity(intent);
    }

    public void btnColorMatrix(View view){
        Intent intent=new Intent(this,ColorMatrix.class);
        startActivity(intent);
    }
}
