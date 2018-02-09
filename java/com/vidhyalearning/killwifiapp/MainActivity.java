package com.vidhyalearning.killwifiapp;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText disableWifiText,coolingTimeText;
    int disableInMinutes,coolingInMinutes;
    Button startServiceBtn,stopServiceBtn;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startServiceBtn = (Button) findViewById(R.id.startService);
        stopServiceBtn = (Button) findViewById(R.id.stopService);
        disableWifiText = (EditText)findViewById(R.id.disabletimerText);
        coolingTimeText = (EditText)findViewById(R.id.coolingTimeText);
        disableWifiText.setText("5");
        coolingTimeText.setText("5");
        disableInMinutes = Integer.parseInt(disableWifiText.getText().toString());
        coolingInMinutes = Integer.parseInt(coolingTimeText.getText().toString());


    }



    public void startService(View view) {
        disableInMinutes = Integer.parseInt(disableWifiText.getText().toString());
        coolingInMinutes = Integer.parseInt(coolingTimeText.getText().toString());
        if(!(disableInMinutes >0 )){
            Toast.makeText(this,"Enter a value greater than 0",Toast.LENGTH_LONG).show();
            return;
        }
        startServiceBtn.setEnabled(false);
        stopServiceBtn.setEnabled(true);
        Intent i =  new Intent(getApplicationContext(), MyService.class);
        i.putExtra("disableTimer",disableInMinutes);
        i.putExtra("coolingTimer",coolingInMinutes);
        try {
            startService(i);
        }
         catch(IllegalStateException ex){
            Toast.makeText(this,"Please Stop service and try.\n Error: " +ex.getMessage(),Toast.LENGTH_SHORT).show();
            return;
        }
    }
;

    public void stopService(View view) {
        startServiceBtn.setEnabled(true);
        stopServiceBtn.setEnabled(false);
        stopService(new Intent(getApplicationContext(), MyService.class));
    }

    public void enableHelp(View view) {
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog1);
        // Set dialog title

        dialog.show();
    }
}
