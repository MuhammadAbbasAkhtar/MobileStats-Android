package com.maa.mobilestats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maa.mobilestats.FetchStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import com.maa.mobilestats.CustomJobService;
import com.maa.mobilestats.HttpPostRequest;
import static androidx.core.content.ContextCompat.getSystemService;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String command;
    TextView voltage_out, current_now, charge_now, battery_technology,battery_design_capacity,
            battery_capacity,battery_temp,status,FreqDisplay,SeekBarLabel,ProgressInfo;
    Button btn;
    SeekBar updateFreqSlider;
    private final int ORANGE = 0xFFFFB608;
    ImageView SyncInfo;
    Timer myTimer;
    TimerTask myTimerTask;

    //#region Server Config
        private static final String SERVER = "http://192.168.100.4:7839";
        private static final String SERVERTAG = "SERVER DEBUG";

    //#endregion

    //#region Units
        String unitCurrent = " mA";
        String unitVoltage = " mV";
        String unitBattery = " mAh";
        String unitCharge = " %";
        String unitTemp = " °C";
    //#endregion
    //#region variables
        float milivolt, battery_designCapacity, batteryCapacity, batteryTemp;
        String battTech,BatteryStatus, miliampere;
        String charge = "";
    //#endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //#region Assign Elements
            btn = (Button)findViewById(R.id.Exec);
            voltage_out = (TextView)findViewById(R.id.voltage);
            current_now = (TextView)findViewById(R.id.current);
            charge_now = (TextView)findViewById(R.id.charge);
            battery_technology = (TextView)findViewById(R.id.batteryTechnology);
            battery_design_capacity = (TextView)findViewById(R.id.batteryDesignCapacity);
            battery_capacity = (TextView)findViewById(R.id.batteryCapacity);
            battery_temp = (TextView)findViewById(R.id.batteryTemp);
            status = (TextView)findViewById(R.id.batteryStatus);
            updateFreqSlider = (SeekBar)findViewById(R.id.seekBar);
            FreqDisplay = (TextView)findViewById(R.id.FreqDisplay);
            SeekBarLabel =(TextView)findViewById(R.id.seekBarLabel);
            SyncInfo = (ImageView)findViewById(R.id.syncInfo);
            ProgressInfo = (TextView)findViewById(R.id.progressInfo);
        //#endregion

        //#region Seekbar setup


            //SeekBarLabel.setText("----------|----|----|-----|-----|-----|-----|-----|");
            SeekBarLabel.setText("----------|----|----|-----|-----|-----|-----|-----|");

            updateFreqSlider.setMax(10);
            updateFreqSlider.setProgress(0);
            updateFreqSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO
                String freq = "";
                if(progressChangedValue != 0){
                    btn.setVisibility(View.INVISIBLE);
                }
                else{
                    btn.setVisibility(View.VISIBLE);
                }
                switch (progressChangedValue){
                    case (0):
                        freq = "manual";
                        cancelJob();
                        break;
                    case (1):
                        freq = "10s";
                        cancelJob();
                        doSmallJob(10000);
                        break;
                    case (2):
                        freq = "30s";
                        cancelJob();
                        doSmallJob(30000);
                        break;
                    case (3):
                        freq = "1 min";
                        cancelJob();
                        doSmallJob(60000);
                        break;
                    case (4):
                        freq = "3 min";
                        cancelJob();
                        doSmallJob(180000);
                        break;
                    case (5):
                        freq = "5 min";
                        cancelJob();
                        doSmallJob(300000);
                        break;
                    case (6):
                        freq = "10 min";
                        cancelJob();
                        doSmallJob(600000);
                        break;
                    case (7):
                        freq = "15 min";
                        cancelJob();
                        scheduleJob(15);
                        break;
                    case (8):
                        freq = "30 min";
                        cancelJob();
                        scheduleJob(30);
                        break;
                    case (9):
                        freq = "45 min";
                        cancelJob();
                        scheduleJob(45);
                        break;
                    case (10):
                        freq = "1 hour";
                        cancelJob();
                        scheduleJob(60);
                        break;

                }
                FreqDisplay.setText(freq);
            }
        });

        //#endregion

        //#region Empty Changing text value fields
            voltage_out.setText("");
            current_now.setText("");
            charge_now.setText("");
            battery_technology.setText("");
            battery_design_capacity.setText("");
            battery_capacity.setText("");
            battery_temp.setText("");
            status.setText("");
            FreqDisplay.setText("manual");
            ProgressInfo.setText("");
        //#endregion

        btn.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view) {
                FetchInfo();
            }
        });


    }


    public  void FetchInfo(){

        FetchStats stats = new FetchStats();

        milivolt = stats.FetchVoltage();

        miliampere = stats.FetchCurrent();

        charge = String.valueOf(stats.FetchChargeLvl((BatteryManager)getSystemService(BATTERY_SERVICE)));

        battTech = stats.FetchBatteryTech();

        battery_designCapacity = stats.FetchBatteryDesignCapacity();

        batteryCapacity = stats.FetchBatteryCapacity();

        batteryTemp = stats.FetchTemperature();

        BatteryStatus = stats.FetchStatus();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
             //#region Assigning values to Elements

                voltage_out.setText(String.valueOf(milivolt) + unitVoltage);
                current_now.setText(String.valueOf(miliampere) + unitCurrent);
                charge_now.setText(charge + unitCharge);
                battery_technology.setText(battTech);
                battery_design_capacity.setText(String.valueOf(battery_designCapacity) + unitBattery);
                battery_capacity.setText(String.valueOf(batteryCapacity) + unitBattery);
                battery_temp.setText(String.valueOf(batteryTemp) + unitTemp);
                status.setText(BatteryStatus);

                switch (BatteryStatus) {
                    case ("Discharging"):
                        status.setTextColor(Color.RED);
                        break;
                    case ("Charging"):
                        status.setTextColor(ORANGE);
                        break;
                    case ("Full"):
                        status.setTextColor(Color.GREEN);
                        break;
                }

        //#endregion

            Animation rotation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
            rotation.setFillAfter(true);

            SyncEffect(true,rotation);
            btn.setVisibility(View.INVISIBLE);
            ProgressInfo.setText("");

            //#region Format And Send Data to Server
                String[] data = new String[11];
                    data[0] = "Redmi Note 4";
                    data[1] = BatteryStatus;
                    data[2] = String.valueOf(milivolt);
                    data[3] = String.valueOf(miliampere);
                    data[4] = charge;
                    data[5] = String.valueOf(batteryTemp);
                    data[6] = battTech;
                    data[7] = String.valueOf(battery_designCapacity);
                    data[8] = String.valueOf(batteryCapacity);
                String syncType = String.valueOf(FreqDisplay.getText());
                if(syncType == "manual"){
                    data[9] = "manual";
                    data[10] = "";
                }
                else{
                    data[9] = "scheduled";
                    data[10] = syncType;
                }


            final Handler handler = new Handler();
            final String[] Data = data;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendData(Data);
                }
            }, 1500);
            //#endregion
            }
        });
    }

    public  void scheduleJob(int delay){
        ComponentName componentName = new ComponentName(this, CustomJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setPeriodic(delay * 60 * 1000)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }
    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        if(myTimer != null){
            myTimer.cancel();
        }
        Log.d(TAG, "Job cancelled");
    }

    public void doSmallJob(int period){

        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                FetchInfo();

            }
        }, 0, period);

        String periodUnit = "";
        int plog = period/1000;

        if(plog < 60){
            periodUnit = " s";
            period = period/1000;
        }
        else if(plog >= 60){
            periodUnit = " min";
            period = plog/60;
        }
        Log.d(TAG, "small Job scheduled for "+ period + periodUnit);
    }


    public boolean checkConnectivity(){
        HttpGetRequest getRequest = new HttpGetRequest();
        String result;
        boolean check = false;

        //Perform the doInBackground method, passing in our url
            try {
                result = getRequest.execute(SERVER).get();
                Log.d(SERVERTAG, result + "");
                if(result != null){
                    check = true;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return check;
    }

    public  void sendData(String [] stats){
        if(checkConnectivity()){
            HttpPostRequest PostRequest = new HttpPostRequest();
            String result = "";

            //Perform the doInBackground method, passing in our url
            try {
                String[] params = new String[13];
                    params[0] = SERVER;
                    params[1] = "/saveInfo";
                    int i = 2;
                    for(int s = 0; s < stats.length; s++ ){
                        params[i]= stats[s];
                        i++;
                    }
                result = PostRequest.execute(params).get();



                final String resultf = result;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (resultf != "") {
                            ProgressInfo.setText("Data Synced");
                            if(FreqDisplay.getText() == "manual") {
                                btn.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            ProgressInfo.setText("Unable to Sync Data");
                        }
                    }
                });


            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(SERVERTAG, result + "");

        }
    }

    public void SyncEffect(final boolean state, final Animation rotation){


        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(state) {
                    SyncInfo.startAnimation(rotation);
                }
                else{
                    SyncInfo.clearAnimation();
                   SyncInfo.clearFocus();
                }
            }
        });

    }



}
