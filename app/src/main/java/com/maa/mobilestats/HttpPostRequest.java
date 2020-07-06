package com.maa.mobilestats;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;


import static java.net.Proxy.Type.HTTP;


public class HttpPostRequest extends AsyncTask<String, Void, String> {

    public static final String REQUEST_METHOD = "POST";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... params) {
        String URL = params[0];
        String Path = params[1];
        String result = "";
        String inputLine;

        //#region Encode Data

            String[] stats = new String[12];
            int paramLength = params.length;
            int flimit = paramLength -3;
            int p = 2;
            // for needs to run 8 times
            // param Length : 11 - 3 = 8

            for(int i = 0; i <= flimit; i++){
                stats[i] = params[p];
                p++;

            }
            String data = null;

            try {
                data = URLEncoder.encode("device", "UTF-8") + "=" + URLEncoder.encode(stats[0], "UTF-8");

                data += "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(stats[1], "UTF-8");
                data += "&" + URLEncoder.encode("voltage", "UTF-8") + "=" + URLEncoder.encode(stats[2], "UTF-8");
                data += "&" + URLEncoder.encode("current", "UTF-8") + "=" + URLEncoder.encode(stats[3], "UTF-8");
                data += "&" + URLEncoder.encode("charge", "UTF-8") + "=" + URLEncoder.encode(stats[4], "UTF-8");
                data += "&" + URLEncoder.encode("temperature", "UTF-8") + "=" + URLEncoder.encode(stats[5], "UTF-8");
                data += "&" + URLEncoder.encode("batteryType", "UTF-8") + "=" + URLEncoder.encode(stats[6], "UTF-8");
                data += "&" + URLEncoder.encode("batteryDesignCapacity", "UTF-8") + "=" + URLEncoder.encode(stats[7], "UTF-8");
                data += "&" + URLEncoder.encode("batteryCapacity", "UTF-8") + "=" + URLEncoder.encode(stats[8], "UTF-8");
                data += "&" + URLEncoder.encode("syncType", "UTF-8") + "=" + URLEncoder.encode(stats[9], "UTF-8");
                data += "&" + URLEncoder.encode("syncSchedule", "UTF-8") + "=" + URLEncoder.encode(stats[10], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        //#endregion

        try {
        URL myUrl = new URL(URL+Path);
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            //connection.setChunkedStreamingMode(0);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
    
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            os.close();
    
            int responseCode=connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader in=new BufferedReader( new InputStreamReader(connection.getInputStream()));
               
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = in.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                in.close();
                streamReader.close();
                result = stringBuilder.toString();
            }
            if(connection != null) { // Make sure the connection is not null.
                    connection.disconnect();
            }
            
        } catch(IOException e) {
            e.printStackTrace();

        }

        return result;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);

        int l = 0;
        if(result.equals("Data Saved")){
            Log.d("SERVER DEBUG","HTTP POST is working...");
        }else{
            Log.d("SERVER DEBUG","Invalid POST req...");
        }
    }

}
