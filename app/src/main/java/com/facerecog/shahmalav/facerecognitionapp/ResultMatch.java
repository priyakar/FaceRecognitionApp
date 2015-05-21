package com.facerecog.shahmalav.facerecognitionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class ResultMatch extends Activity {
    private ProgressBar mProgressBar;
    private Handler handler = new Handler();
TextView percentMatch ;
private String value =  null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_match);
        percentMatch = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.circle_progress_bar);
        Intent i = getIntent();
        if (i != null) {
            Bundle gotBasket = i.getExtras();
            value = gotBasket.getString("results");

            parseResults(value);
        }
    }
    /*public ResultMatch(){

            Toast.makeText(this, "checkthisout",Toast.LENGTH_LONG).show();
            parseResults(value);

    }*/

public void parseResults (String result){
    mProgressBar.setProgress((int)(Double.parseDouble(result)*100));
    percentMatch.setText((int)(Double.parseDouble(result)*100)+"%");
}
}
