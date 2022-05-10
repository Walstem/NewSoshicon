package com.bulat.soshicon2.BottomNavigation.event;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class receivingEvent extends AsyncTask<String, String, String> {
    String filename;
    String[] Args;
    String[] KeysArgs;

    public receivingEvent(String filename, String[] KeysArgs, String[] Args){
        this.filename = filename;
        this.Args = Args;
        this.KeysArgs = KeysArgs;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(Args.length);
        for (int i=0; i< Args.length; i++){
            nameValuePairs.add(new BasicNameValuePair(KeysArgs[i], Args[i]));
        }

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Execute and get the response.
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert response != null;
            HttpEntity entity = response.getEntity();

            int n = 0;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(entity.getContent(), "UTF8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);

            httpclient.getConnectionManager().shutdown();
            return writer.toString();

        } catch (IOException e) {
            e.printStackTrace();

            httpclient.getConnectionManager().shutdown();
            return null;
        }
    }
}